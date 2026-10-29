import {setGlobalOptions} from "firebase-functions";
import {dailyTodoCleanup} from "./todos/dailyCleanup";
import {sendTodoReminders} from "./todos/sendReminders";
import {sendEventReminders} from "./todos/sendEventReminders";
import {deleteUserAccount} from "./users/deleteUserAccount";
import {onDocumentCreated} from "firebase-functions/v2/firestore";
import admin from "./admin";

export {dailyTodoCleanup, sendTodoReminders};
export {sendEventReminders, deleteUserAccount};

admin.initializeApp();

setGlobalOptions({maxInstances: 10});

console.log("notifyNewArticle file loaded");

/**
 * Converts a user-facing tag into a valid FCM topic name.
 * - lowercases text
 * - replaces spaces with underscores
 * - removes invalid characters
 *
 * @param {string} tag - The original tag from Firestore
 * @return {string} A sanitized FCM topic name
 */
function topicName(tag: string): string {
  return tag
    .trim()
    .toLowerCase()
    .replace(/\s+/g, "")
    .replace(/[^a-z0-9\-_.~%]/g, "");
}

export const notifyNewArticle = onDocumentCreated(
  "articles/{articleId}",
  async (event) => {
    console.log("Function Triggered");
    const article = event.data?.data();

    if (!article) return;

    const title = article.title ?? "New Article";
    const tags = article.tags ?? [];

    for (const tag of tags) {
      const topic = topicName(tag);

      // Find all users that have this role/tag
      const usersSnapshot = await admin.firestore()
        .collection("users")
        .where("activeRoles", "array-contains", tag)
        .get();

      const recipients = usersSnapshot.docs.map((doc) => ({
        uid: doc.id,
        email: doc.data().email,
        name: doc.data().name,
      }));

      console.log(`Users receiving topic "${tag}":`, recipients);
      await admin.messaging().send({
        topic,
        notification: {
          title: "New Article Posted",
          body: title,
        },
        data: {
          screen: "home",
        },
        android: {
          priority: "high",
          notification: {
            channelId: "eaglenews",
            sound: "default",
          },
        },
        apns: {
          payload: {
            aps: {
              alert: {
                title: "New Article Posted",
                body: title,
              },
              sound: "default",
            },
          },
        },
      });

      console.log(`Sent notification to topic ${tag}`);
    }
  }
);

console.log("notifyNewLink file loaded");

export const notifyNewLink = onDocumentCreated(
  "links/{linkId}",
  async (event) => {
    console.log("Link notification triggered");

    const linkData = event.data?.data();

    if (!linkData) {
      console.log("No link data");
      return;
    }

    const name = linkData.name ?? "New Link";
    const tags = linkData.tags ?? [];

    for (const tag of tags) {
      const topic = topicName(tag);

      await admin.messaging().send({
        topic,
        notification: {
          title: "New Link Posted",
          body: name,
        },
        data: {
          screen: "links",
        },
        android: {
          priority: "high",
          notification: {
            channelId: "eaglenews",
            sound: "default",
          },
        },
        apns: {
          payload: {
            aps: {
              alert: {
                title: "New Link Posted",
                body: `${name}: ${linkData.link}`,
              },
              sound: "default",
            },
          },
        },
      });

      console.log(`Sent link notification to topic ${tag}`);
    }
  }
);

console.log("notifyGroupEvent file loaded");

export const notifyGroupEvent = onDocumentCreated(
  "groupNotifications/{notificationId}",
  async (event) => {
    const data = event.data?.data();

    if (!data) return;

    const recipientIds = data.recipientIds ?? [];
    const title = data.title ?? "New Event";
    const publisher = data.publisher ?? "Someone";

    for (const userId of recipientIds) {
      if (userId === data.publisherId) continue;

      await admin.messaging().send({
        topic: `user_${userId}`,
        notification: {
          title: "New Event Posted",
          body: `${title} | Posted by ${publisher}`,
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
    }
  }
);
