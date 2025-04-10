# TastySphere API Documentation

## Table of Contents
- [Authentication](#authentication)
- [Admin](#admin)
- [Posts](#posts)
- [Users](#users)
- [Social](#social)
- [Orders](#orders)
- [Notifications](#notifications)
- [Roles](#roles)

## Authentication

### Login
- **URL:** `/auth/login`
- **Method:** POST
- **Body:**
  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```
- **Response:**
  ```json
  {
    "token": "string"
  }
  ```

## Admin
Requires ADMIN role for all endpoints.

### Get System Statistics
- **URL:** `/api/admin/statistics`
- **Method:** GET
- **Response:** Map of system statistics

### Get Detailed Statistics
- **URL:** `/api/admin/statistics/detailed`
- **Method:** GET
- **Response:** Map of detailed system statistics

### Get System Metrics
- **URL:** `/api/admin/metrics`
- **Method:** GET
- **Response:** Map of system metrics

### Get Pending Posts
- **URL:** `/api/admin/posts/pending`
- **Method:** GET
- **Parameters:**
  - `page` (optional, default: 0)
  - `size` (optional, default: 10)
- **Response:** Paginated list of pending posts

### Audit Post
- **URL:** `/api/admin/posts/{postId}/audit`
- **Method:** POST
- **Parameters:**
  - `postId` (path)
  - `approved` (query)
  - `reason` (query)

### Manage Users
- **URL:** `/api/admin/users`
- **Method:** GET
- **Parameters:**
  - `page` (optional, default: 0)
  - `size` (optional, default: 10)
- **Response:** Paginated list of users

### Update User Status
- **URL:** `/api/admin/users/{userId}/status`
- **Method:** POST
- **Parameters:**
  - `userId` (path)
  - `active` (query)

### Get Audit Logs
- **URL:** `/api/admin/audit-logs`
- **Method:** GET
- **Parameters:**
  - `page` (optional, default: 0)
  - `size` (optional, default: 20)
- **Response:** Paginated list of audit logs

## Posts

### Get Posts
- **URL:** `/api/posts`
- **Method:** GET
- **Parameters:**
  - `page` (optional, default: 0)
  - `size` (optional, default: 10)
- **Response:** Paginated list of posts

### Get Single Post
- **URL:** `/api/posts/{postId}`
- **Method:** GET
- **Parameters:** `postId` (path)
- **Response:** Post details

### Create Post
- **URL:** `/api/posts`
- **Method:** POST
- **Body:** PostDTO
- **Response:** Created post

### Update Post
- **URL:** `/api/posts/{postId}`
- **Method:** PUT
- **Parameters:** `postId` (path)
- **Body:** PostDTO
- **Response:** Updated post

### Delete Post
- **URL:** `/api/posts/{postId}`
- **Method:** DELETE
- **Parameters:** `postId` (path)

### Search Posts
- **URL:** `/api/posts/search`
- **Method:** GET
- **Parameters:**
  - `keyword` (required)
  - `page` (optional, default: 0)
  - `size` (optional, default: 10)
- **Response:** Paginated search results

### Get Recommended Posts
- **URL:** `/api/posts/recommended`
- **Method:** GET
- **Response:** List of recommended posts

### Like/Unlike Post
- **URL:** `/api/posts/{postId}/like`
- **Method:** POST (like) / DELETE (unlike)
- **Parameters:** `postId` (path)

## Users

### Get User Profile
- **URL:** `/api/users/profile`
- **Method:** GET
- **Response:** User profile details

### Update Profile
- **URL:** `/api/users/profile`
- **Method:** PUT
- **Body:** UserDTO
- **Response:** Updated profile

### Get User Posts
- **URL:** `/api/users/{userId}/posts`
- **Method:** GET
- **Parameters:**
  - `userId` (path)
  - `page` (optional, default: 0)
  - `size` (optional, default: 10)
- **Response:** Paginated list of user's posts

### Follow/Unfollow User
- **URL:** `/api/users/{userId}/follow`
- **Method:** POST (follow) / DELETE (unfollow)
- **Parameters:** `userId` (path)

## Social

### Create Comment
- **URL:** `/api/social/posts/{postId}/comments`
- **Method:** POST
- **Parameters:**
  - `postId` (path)
  - `parentId` (optional)
  - `content` (required)
- **Response:** Created comment

### Toggle Like
- **URL:** `/api/social/posts/{postId}/like`
- **Method:** POST
- **Parameters:** `postId` (path)

### Toggle Follow
- **URL:** `/api/social/users/{userId}/follow`
- **Method:** POST
- **Parameters:** `userId` (path)

### Get Comments
- **URL:** `/api/social/posts/{postId}/comments`
- **Method:** GET
- **Parameters:**
  - `postId` (path)
  - Pagination parameters
- **Response:** Paginated list of comments

## Notifications

### Get Notifications
- **URL:** `/api/notifications`
- **Method:** GET
- **Parameters:** Pagination parameters
- **Response:** Paginated list of notifications

### Mark Notification as Read
- **URL:** `/api/notifications/{notificationId}/read`
- **Method:** POST
- **Parameters:** `notificationId` (path)

### Get Unread Count
- **URL:** `/api/notifications/unread-count`
- **Method:** GET
- **Response:** Number of unread notifications

## Orders

### Get User Orders
- **URL:** `/orders/user/{userId}`
- **Method:** GET
- **Parameters:** `userId` (path)
- **Response:** List of orders

### Get Order
- **URL:** `/orders/{id}`
- **Method:** GET
- **Parameters:** `id` (path)
- **Response:** Order details

### Create Order
- **URL:** `/orders`
- **Method:** POST
- **Body:** Order object
- **Response:** Created order

### Delete Order
- **URL:** `/orders/{id}`
- **Method:** DELETE
- **Parameters:** `id` (path)

## Roles

### Get All Roles
- **URL:** `/roles`
- **Method:** GET
- **Response:** List of roles

### Create Role
- **URL:** `/roles`
- **Method:** POST
- **Parameters:**
  - `name` (required)
  - `description` (required)
- **Response:** Success/failure message

### Assign Permissions
- **URL:** `/roles/assign`
- **Method:** POST
- **Parameters:**
  - `roleName` (required)
  - `permissionNames` (required)
- **Response:** Success/failure message 