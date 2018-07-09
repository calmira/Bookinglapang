'use-strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotif = functions.database.ref("users/{user_id}/notifications/{notification_id}").onWrite((change, context) =>{
    const user_id = context.params.user_id;
    const notification_id = context.params.notification_id;

    console.log("User ID : " + user_id + " | Notification ID : " + notification_id);

    return admin.database().ref("users/" + user_id + "/notifications/" + notification_id).once("value").then(function(snapshot) {
        const from_user_id = snapshot.child("from").val();
        const from_message = snapshot.child("message").val();

        const from_data = admin.database().ref("users/" + from_user_id).once("value");
        const to_data = admin.database().ref("users/" + user_id).once("value");
        
        return Promise.all([from_data, to_data]).then(result => {
            const from_name = result[0].child("name").val();
            const to_name = result[1].child("name").val();
            const token_id = result[1].child("tokenId").val();
            const payload = {
                notification: {
                    title : "Notification From : " + from_name,
                    body : from_message,
                    icon : "default"
                }
            };

            return admin.messaging().sendToDevice(token_id, payload).then(result => {
                console.log("Notification sent.");
            });
        });
    
    });
});