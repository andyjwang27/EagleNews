import {onSchedule} from "firebase-functions/v2/scheduler";
import {logInfo} from "../logger";
import * as admin from "firebase-admin";

admin.initializeApp();

export const dailyTodoCleanup = onSchedule(
  {
    schedule: "0 0 * * *", // Every day at midnight
    timeZone: "America/New_York",
  },
  async () => {
    logInfo("Daily cleanup started");

    const db = admin.firestore();

    const usersSnap = await db.collection("users").get();

    const now = new Date();

    for (const userDoc of usersSnap.docs) {
      const todosSnap = await userDoc.ref.collection("todos").get();

      const batch = db.batch();

      todosSnap.forEach((doc) => {
        const data = doc.data();

        if (!data.isDone || !data.dueDate) return;

        const dueDate = new Date(data.dueDate * 1000);

        // Delete only if completed and due before today
        if (dueDate < now) {
          batch.delete(doc.ref);
          logInfo("Deleting todo", doc.id);
        }
      });

      await batch.commit();
    }
  }
);
