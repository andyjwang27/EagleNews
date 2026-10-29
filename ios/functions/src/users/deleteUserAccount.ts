import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

admin.initializeApp();

export const deleteUserAccount = functions.https.onCall(
  async (request: functions.https.CallableRequest<any>) => {
    if (!request.auth) {
      throw new functions.https.HttpsError(
        "unauthenticated",
        "User must be authenticated"
      );
    }

    const uid = request.auth.uid;

    const db = admin.firestore();
    const userRef = db.collection("users").doc(uid);

    const todosSnap = await userRef.collection("todos").get();
    for (const doc of todosSnap.docs) {
      await doc.ref.delete();
    }

    const eventsSnap = await userRef.collection("events").get();
    for (const doc of eventsSnap.docs) {
      await doc.ref.delete();
    }

    await userRef.delete();
    try {
      await admin.auth().deleteUser(uid);
    } catch (e) {
      console.log("Auth delete failed but continuing", e);
    }

    return {success: true};
  }
);
