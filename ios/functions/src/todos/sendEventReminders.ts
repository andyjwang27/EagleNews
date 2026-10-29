import {onSchedule} from "firebase-functions/v2/scheduler";
import * as admin from "firebase-admin";

export const sendEventReminders = onSchedule(
  "every 1 minutes",
  async () => {
    const now = Math.floor(Date.now() / 1000);

    const usersSnapshot = await admin.firestore()
      .collection("users")
      .get();

    for (const userDoc of usersSnapshot.docs) {
      const userId = userDoc.id;

      const eventsSnapshot = await userDoc.ref
        .collection("events")
        .where("reminderSent", "==", false)
        .get();

      for (const eventDoc of eventsSnapshot.docs) {
        const event = eventDoc.data();

        const eventMinutesBefore = Math.max(event.eventMinutesBefore ?? 5, 5);

        const offsetSeconds = eventMinutesBefore * 60;

        const eventTime = event.startDate.seconds;
        const triggerTime = eventTime - offsetSeconds;

        if (triggerTime <= now) {
          const eventDate = event.startDate.toDate();

          const formattedDate = eventDate.toLocaleString("en-US", {
            timeZone: "America/New_York",
            month: "long",
            day: "numeric",
            year: "numeric",
            hour: "numeric",
            minute: "2-digit",
          });

          try {
            await admin.messaging().send({
              topic: `user_${userId}`,
              notification: {
                title: "Upcoming Event",
                body: `${event.title} at ${formattedDate}`,
              },
              data: {
                screen: "calendar",
              },
              android: {
                priority: "high",
                notification: {
                  channelId: "eaglenews",
                  sound: "default",
                },
              },
            });

            await eventDoc.ref.update({
              reminderSent: true,
            });

            console.log(
              `Sent event reminder for ${event.title} to ${userId}`
            );
          } catch (error) {
            console.error(
              `Failed to send event reminder for ${event.title} to ${userId}:`,
              error
            );
          }
        }
      }
    }
  }
);
