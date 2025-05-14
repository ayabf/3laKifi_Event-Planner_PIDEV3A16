# 3alaKifi – Event Planner
<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/othneildrew/Best-README-Template">
    <img src="C:/Users/PC/Downloads/logo2.png" alt="Logo" width="200" height="200">
  </a>
</div>

## 🎯 Overview
**3alaKifi** is an intelligent event planning platform developed using **JavaFX** as part of the academic project for the **PIDEV 3A** course at [Esprit School of Engineering](https://esprit.tn). It helps users organize and personalize their events through a unified, intuitive interface, combining intelligent assistance with centralized planning for a smoother, more efficient experience.

> Keywords: JavaFX, event-planning, AI, weather-API, FullCalendar, PDF export, QR code, Stripe payment, inventory-management, role-based-access, chatbot, forum, statistics, supplier-tracking, Java 17, MySQL


---
## Cross Platform:
1) [Web](https://github.com/Mira197/PIDEV-Symfony-3A16-Event-Planner-Hack-Pack)
2) [Desktop](https://github.com/ayabf/3laKifi_Event-Planner_PIDEV3A16)
   
## 🚀 Features
The platform provides 5 main management modules:
### 1. Event Management 
- 🔍 **Venue suggestion system** powered by AI (based on event date, city, and capacity)
- ☁️ **Weather forecast integration** to help users check the weather conditions on the selected date and city for their event.
- 📅 **FullCalendar integration** to create and visualize events on a calendar view with real-time updates.
- 🧾 **PDF export** for both calendar and booking summaries.
- 🔗 QR code embedded in PDF – Each exported booking summary includes a QR code that, when scanned, redirects to the full booking details.
- 🔁 **Dynamic search and filtering**  for events, locations, and bookings for faster and smoother user experience.
- 📊 **Statistics and analytics** with visual insights to help admins track usage and better understand user behavior.
- 👨‍💼 Admin panel for managing events, bookings, and locations
### 2. Order Management

- 🛒 **Smart order creation** based on the user’s active cart with a smooth and intuitive checkout process.
- 💳 **Secure online payment integration** via Stripe, supporting both full and partial payments.
- 🔐 **Unique QR code generation** for every order to ensure fast and secure validation during delivery or on-site check-in.
- 🔍 **Advanced admin tools** for dynamic filtering of orders by date, status, or user, and real-time keyword-based search.
- 📊 **Admin dashboard** offering a monthly overview of orders, with detailed stats on confirmed, pending, and canceled purchases.
- 📲 SMS campaign system to automatically notify users about new promotional offers, discounts, and loyalty benefits in real time.
### 3. Product Management

- 📦 **Dedicated product interface** for adding, editing, and removing items tied to a specific stock.
- 🗂️ **Dynamic product listing** by category with integrated pagination and real-time currency conversion (multi-currency support).
- 🚨 **Smart stock alert system** that triggers automatic email and in-app notifications when stock levels reach a critical threshold.
- 📊 **Interactive product analytics** showing distribution by category, price range, and supplier through dynamic charts and graphs.
- 🤝 **Supplier-specific tracking**, enabling each connected supplier to monitor and manage their own product listings and stock levels.

### 4. User Management

- ✅ **User registration, login, and logout** with a smooth and secure authentication flow.
- 📝 **Profile editing and profile picture upload**, allowing users to personalize their account.
- 🎭 **Role-based access control** with distinct interfaces and permissions for admins, clients, and suppliers.
- ✉️ **Password reset via email(Google)** – users receive a verification code to safely recover and reset their forgotten password.
- 💬 **Integrated chatbot** available across the platform to assist users with navigation, basic questions, and platform usage.

### 5. Forum Management

- 💬 **User-generated discussions** – users can create and publish topics to initiate conversations within the community.
- 🚨 **Bad words detection system** to automatically detect offensive language in posts and comments.
- 🗑️ **Automatic removal of flagged content** – publications reported by users are reviewed and deleted if necessary.
- 🚩 **Report system** allowing users to flag inappropriate or abusive content for review.
- 🛡️ **Admin moderation panel** for managing discussions, reviewing reports, and blocking or warning users when necessary.
- 🌐 Multilingual post translation – Users can view or translate forum posts into multiple languages for better accessibility.


---

## 🛠️ Tech Stack
[![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=java&logoColor=white)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![JavaFX](https://img.shields.io/badge/JavaFX-17-2E7EEA?style=for-the-badge&logo=java&logoColor=white)](https://openjfx.io/)
[![Scene Builder](https://img.shields.io/badge/Scene%20Builder-F39200?style=for-the-badge&logo=oracle&logoColor=white)](https://gluonhq.com/products/scene-builder/)
[![JDBC](https://img.shields.io/badge/JDBC-MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://dev.mysql.com/doc/connector-j/en/)
[![MySQL](https://img.shields.io/badge/MySQL-Server-003545?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)

---

## 📁 Directory Structure
```
├── src/
│ ├── controllers/
│ ├── models/
│ ├── services/
│ ├── utils/
│ ├── views/ # FXML files
│ ├── Main.java
│ └── App.java
├── resources/
│ ├── images/
│ ├── styles/
│ └── config/
└── README.md
```

## 📦 Getting Started

### Requirements

- Java 17
- JavaFX SDK 17+
- MySQL Server
- SceneBuilder
- Maven or Gradle

### Installation

1. **Clone the repository**:
   ```sh
   git clone https://github.com/ayabf/3laKifi_Event-Planner_PIDEV3A16.git
   ```
   
2. **Import into IDE**(IntelliJ or Eclipse recommended):

3. **Configure database connection:**:
   in DataSource.java

4. **Run Main.java**:
   ```sh
   Right-click > Run 'Main'
   ```


<!-- ### 👥 Top Contributors

<a href="https://github.com/Mira197/PIDEV-Symfony-3A16-Event-Planner-Hack-Pack/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=Mira197/PIDEV-Symfony-3A16-Event-Planner-Hack-Pack" alt="Top Contributors" />
</a>-->


## Acknowledgments
This project was completed under the guidance of [Professor: Mr Hamza Chenneaoui]
(mail:Hamza.CHENENAOUI@esprit.tn) at Esprit.

<!-- ## Here are some screenshots of our application:

<p align="center">
 <img src="public/images//img1.jpg">
 <img src="public/images//img2.jpg">
 <img src="public/images//img4.jpg">
 <img src="public/images//img5.jpg">
</p>  -->





