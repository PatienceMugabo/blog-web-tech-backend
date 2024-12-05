# Blogging Website Backend API

This is the backend API for a Blogging Website built using Spring Boot. It provides endpoints for managing users, posts, comments, likes, and followers.

## Features

1. **User Management**
   - User registration
   - User authentication (JWT-based)
   - User profile management
   - Profile picture upload

2. **Post Management**
   - Create, read, update, delete posts
   - Draft, publish, and archive posts
   - Tagging posts with categories 
   - Retrieve posts by user or category

3. **Comment Management**
   - Add, edit, delete comments on posts
   - Nested comments support
   - Get comments for a specific post

4. **Likes**
   - Like/unlike posts
   - Retrieve likes for a specific post

5. **Followers**
   - Follow/unfollow other users :pending
   - Get followers/following list for a user :pending

## Tech Stack

- **Spring Boot**: Framework for building robust Java applications
- **Spring Security**: For authentication and authorization
- **Spring Data JPA**: For working with databases using JPA
- **Hibernate**: ORM (Object-Relational Mapping) tool for Java
- **PostgreSQL**: Relational database management system
- **JWT (JSON Web Tokens)**: For secure authentication

## Getting Started

1. Clone the repository:

    ```bash
    git clone https://github.com/Munezero2000/Blogging-Application-Backend.git
    ```

2. Navigate to the project directory:

    ```bash
    cd blogging-website-backend
    ```

3. Configure your database settings in `application.properties`.

4. Build and run the project:

    ```bash
    ./mvn spring-boot:run
    ```

5. The API will be available at `http://localhost:8081`.

## API Endpoints

- Detailed documentation of API endpoints can be found in [API Documentation](http://localhost:8081/swagger-ui/index.html).

## Contributing

Contributions are welcome! Feel free to open issues or submit pull requests for any improvements or new features you'd like to see.

## License

This project is licensed under the [MIT License](LICENSE).
