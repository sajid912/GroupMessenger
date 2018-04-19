# GroupMessenger1-Distributed Systems
Design a group messenger that can send messages to multiple AVDs and store received messages in a persistent key-value storage

This project focuses on the following:
• Implement an Android ContentProvider.
• Use file I/O to create a persistent key-value store.
• Manage multiple sockets and multiple client connections.

The content provider takes a string key and a string value, and stores them for later retrieval (even across reboots!).
Only insert() and query() methods are implemented. 

The key-value pairs are stored and retrieved using file I/O operations.

The app multicasts every user-entered message to all app instances, including the one that is sending the message.
It can also handle concurrent messages (that is, multiple messages incoming from multiple apps at the same time).

The app assigns a sequence number to every message it receives. The sequence numbers start from 0 and increase by 1 for each received message

Each message is stored with its sequence number as a key-value pair in the content provider. 
The key is  the sequence number for the message (as a Java string) and the value the message received.

All app instances store every message and its corresponding sequence number individually.
