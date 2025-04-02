# Database_Failover
# Database_Failover
Database Failover System

Overview

This project is a Database Failover System implemented in Core Java. It connects two NoSQL databases, MongoDB and Elasticsearch, ensuring fault tolerance. If one of the databases experiences a breakdown, the system automatically redirects data storage operations to the functional database, maintaining data consistency and availability.

Features

Automatic Failover: Detects when a database goes down and switches operations to the other.

Dual Database Support: Works with MongoDB and Elasticsearch.

Fault-Tolerant Storage: Ensures data is stored even when one database is unavailable.

Seamless Recovery: Once the failed database is restored, data synchronization is handled.

Efficient Handling: Uses exception handling and logging to manage failures effectively.

Tech Stack

Java (Core Java)

Jetty Server (for handling requests)

MongoDB (NoSQL database)

Elasticsearch (Search and analytics engine)

JDBC / MongoDB Java Driver / Elasticsearch Client API
