# TravelAgentFront

TravelAgentFront is an Android application designed for travel agents to manage vacation packages. It includes features for adding new packages, viewing existing packages, and navigation through a bottom navigation bar. The application is built with a clean and intuitive user interface.

## Features

- **View Vacation Packages:** Agents can view a list of all vacation packages they have created.
- **Add Vacation Packages:** Agents can add new vacation packages by filling in details such as package name, destination, price, hotel name, stars, connection flight, start date, and end date.
- **Navigation:** The app includes a bottom navigation bar for easy navigation between different activities.
- **User Authentication:** User-specific data handling.

## Technologies Used

- **Java:** The main programming language used for development.
- **Android SDK:** Provides the API libraries and tools necessary to build Android apps.
- **Retrofit:** For making HTTP requests to fetch and send data to a backend server.
- **Gson:** For converting Java objects to JSON and vice versa.
- **Material Components for Android:** To implement Material Design UI components.

## Getting Started

### Prerequisites

- Android Studio
- Android device or emulator running Android 5.0 (Lollipop) or higher

### Installation

1. **Clone the repository:**
    ```sh
    git clone https://github.com/yourusername/TravelAgentFront.git
    ```
2. **Open the project in Android Studio:**
    - Launch Android Studio.
    - Select Open an existing Android Studio project.
    - Navigate to the cloned repository and select it.
3. **Build the project:**
    - Click on Build in the menu bar.
    - Select Make Project.
4. **Run the app:**
    - Connect your Android device or start an emulator.
    - Click on Run in the menu bar.
    - Select Run app.

## Usage

### Viewing Vacation Packages

- Launch the app.
- The main screen will display a list of vacation packages created by the logged-in user.
- If no packages are found, a message will be displayed with an option to add new packages.

### Adding a Vacation Package

- Navigate to the "Add packages" screen using the bottom navigation bar.
- Fill in the details of the vacation package:
    - Package Name
    - Destination (Select from the dropdown)
    - Price
    - Hotel Name
    - Stars (0 to 5)
    - Connection Flight (Yes or No)
    - Start Date
    - End Date
- Click on the "Publish" button to add the package.

### Navigation

- **Home:** View all vacation packages.
- **Add packages:** Add a new vacation package.
- **Notifications:** View notifications (future feature).
- **Bag manager:** Manage your bags (future feature).

## Code Structure

### Activities

- `AgentActivity`: Activities related to the agent's functionalities.

### Adapters

- Adapter classes for RecyclerViews.

### Models

- `Model`: Contains model classes representing data structures.

### Network

- `Network`: Contains network service implementations for API calls.

### Layout

- Layout files for the UI.

## Endpoints

### User Endpoints

- **Create User**
  - **URL**: `superapp/users`
  - **Method**: `POST`
  - **Description**: Creates a new user.
  - **Body**:
    ```json
    {
      "email": "user@example.com",
      "username": "username",
      "avatar": "avatar_url",
      "role": "user"
    }
    ```

- **Update User**
  - **URL**: `superapp/users/{superapp}/{email}`
  - **Method**: `PUT`
  - **Description**: Updates user information.
  - **Body**:
    ```json
    {
      "username": "new_username",
      "avatar": "new_avatar_url",
      "role": "user"
    }
    ```

- **Get User by ID**
  - **URL**: `superapp/users/login/{superapp}/{email}`
  - **Method**: `GET`
  - **Description**: Retrieves user information by email.

### Object Endpoints

- **Create Object**
  - **URL**: `superapp/objects`
  - **Method**: `POST`
  - **Description**: Creates a new object.
  - **Body**:
    ```json
    {
      "objectId": {
        "superapp": "superapp_name",
        "id": "object_id"
      },
      "type": "object_type",
      "alias": "object_alias",
      "location": {
        "lat": 0.0,
        "lng": 0.0
      },
      "active": true,
      "creationTimestamp": "timestamp",
      "createdBy": {
        "userId": {
          "superapp": "superapp_name",
          "email": "creator_email"
        }
      },
      "objectDetails": {}
    }
    ```

- **Update Object**
  - **URL**: `superapp/objects/{superapp}/{id}`
  - **Method**: `PUT`
  - **Description**: Updates an existing object.
  - **Body**:
    ```json
    {
      "type": "new_object_type",
      "alias": "new_object_alias",
      "location": {
        "lat": 0.0,
        "lng": 0.0
      },
      "active": true,
      "creationTimestamp": "new_timestamp",
      "createdBy": {
        "userId": {
          "superapp": "superapp_name",
          "email": "creator_email"
        }
      },
      "objectDetails": {}
    }
    ```

### Command Endpoints

- **Create Command**
  - **URL**: `superapp/miniapp/{miniAppName}`
  - **Method**: `POST`
  - **Description**: Creates a new command.
  - **Body**:
    ```json
    {
      "commandId": {
        "superapp": "superapp_name",
        "miniapp": "miniapp_name",
        "id": "command_id"
      },
      "command": "command_name",
      "targetObject": {
        "objectId": {
          "superapp": "superapp_name",
          "id": "target_object_id"
        }
      },
      "invocationTimestamp": "timestamp",
      "invokedBy": {
        "userId": {
          "superapp": "superapp_name",
          "email": "invoker_email"
        }
      },
      "commandAttributes": {}
    }
    ```

- **Find Objects by Creator Email and Type**
  - **URL**: `superapp/miniapp/userApp`
  - **Method**: `POST`
  - **Description**: Finds objects created by a specific email and type.
  - **Body**:
    ```json
    {
      "commandId": {
        "superapp": "tripMaster",
        "miniapp": "userApp",
        "id": "unique_command_id"
      },
      "command": "findObjectsByCreatorEmailAndType",
      "targetObject": {
        "objectId": {
          "superapp": "tripMaster",
          "id": "4893f3dd-ceec-4d9e-992a-fae8f08137eb"
        }
      },
      "invocationTimestamp": "timestamp",
      "invokedBy": {
        "userId": {
          "superapp": "tripMaster",
          "email": "invoker_email"
        }
      },
      "commandAttributes": {
        "email": "creator_email",
        "type": "object_type",
        "page": 0,
        "size": 10
      }
    }
    ```


## Contributing

1. Fork the repository.
2. Create your feature branch:
    ```sh
    git checkout -b feature/your-feature
    ```
3. Commit your changes:
    ```sh
    git commit -m 'Add some feature'
    ```
4. Push to the branch:
    ```sh
    git push origin feature/your-feature
    ```
5. Open a pull request.
