import {onSchedule} from "firebase-functions/v2/scheduler";
import * as admin from "firebase-admin";

export const sendTodoReminders = onSchedule(
  "every 1 minutes",
  async () => {
    const now = Math.floor(Date.now() / 1000);

    const usersSnapshot =
      await admin.firestore().collection("users").get();

    for (const userDoc of usersSnapshot.docs) {
      const userId = userDoc.id;

      const todosSnapshot = await userDoc.ref
        .collection("todos")
        .where("isDone", "==", false)
        .where("reminderSent", "==", false)
        .where("dueDate", "<=", now)
        .get();

      for (const todoDoc of todosSnapshot.docs) {
        const todo = todoDoc.data();

        await admin.messaging().send({
          topic: `user_${userId}`,
          notification: {
            title: "New Todo Item",
            body: todo.title,
          },
          data: {
            screen: "todo",
          },
          android: {
            priority: "high",
            notification: {
              channelId: "eaglenews",
              sound: "default",
            },
          },
        });

        await todoDoc.ref.update({
          reminderSent: true,
        });

        console.log(
          `Reminder sent for ${todo.title} to user ${userId}`
        );
      }
    }
  }
);
