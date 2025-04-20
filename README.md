Mini Notes App (Spring Boot + MongoDB)

A simple REST API for managing notes using Spring Boot and MongoDB. Features include:

- CRUD operations
- Pagination & search
- Soft delete
- Data validation


Method | Endpoint | Description
GET | /notes | Get all notes (with pagination & search)

POST | /notes | Add a new note (or multiple)

GET | /notes/{id} | Get a note by ID

PUT | /note/{id} | Update a note

DELETE | /notes/{id} | Soft delete a note


Setup

```bash
git clone https://github.com/your-username/mini-notes-app.git
cd mini-notes-app
./gradlew bootRun


