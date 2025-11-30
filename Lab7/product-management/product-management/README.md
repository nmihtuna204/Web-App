**Project: Product Management**

This README explains how to run the application locally (Windows PowerShell) and describes the code flow for each CRUD operation with file references.

**Run (development)**:
- **Set folder**: `Set-Location -Path .\product-management`
- **Start**: `./mvnw.cmd spring-boot:run`

Notes: Use PowerShell from the repository root (one level above the `product-management` module). If `mvn` is installed globally you may use `mvn spring-boot:run` instead.

**Build & Run (packaged JAR)**:
- **Set folder**: `Set-Location -Path .\product-management`
- **Build**: `./mvnw.cmd -DskipTests package`
- **Run jar**: `java -jar .\target\product-management-0.0.1-SNAPSHOT.jar`

**Run tests**:
- `Set-Location -Path .\product-management`
- `./mvnw.cmd test`

**Accessing the web UI**:
- Default URL: `http://localhost:8080/products`
- Templates are in `src/main/resources/templates` (Thymeleaf): `product-list.html` and `product-form.html`.

**Quick troubleshooting**:
- If `./mvnw.cmd` is not found, make sure you're in the inner `product-management` module directory.
- To change port: `java -Dserver.port=8081 -jar .\target\product-management-0.0.1-SNAPSHOT.jar` or pass JVM args to `spring-boot:run`.

**CRUD code flow**

Create (Add a new product):
- User action: open `http://localhost:8080/products/new` (link handled by `ProductController`).
- Controller: `com.example.productmanagement.controller.ProductController#showNewForm` returns view `product-form` with an empty `Product` model.
- Form submit: the form posts to `/products/save`.
- Controller: `ProductController#saveProduct` receives `@ModelAttribute Product` and calls `productService.saveProduct(product)`; on success it redirects to `/products` with a flash message.
- Service: `com.example.productmanagement.service.ProductServiceImpl#saveProduct` calls `productRepository.save(product)`.
- Repository: `com.example.productmanagement.repository.ProductRepository` extends `JpaRepository` (CRUD implemented by Spring Data JPA), which persists the entity to the database.

Read (List / View products):
- User action: navigate to `http://localhost:8080/products`.
- Controller: `ProductController#listProducts` calls `productService.getAllProducts()` and places the list into the model under `products`.
- Service: `ProductServiceImpl#getAllProducts` calls `productRepository.findAll()` to fetch data.
- View: `src/main/resources/templates/product-list.html` renders the `products` list.

Update (Edit a product):
- User action: click edit link which goes to `/products/edit/{id}`.
- Controller: `ProductController#showEditForm` calls `productService.getProductById(id)`; if found it adds the `product` to the model and returns `product-form` with populated fields.
- Form submit: posts to `/products/save` with the product `id` present.
- Controller: `ProductController#saveProduct` calls `productService.saveProduct(product)` which invokes `productRepository.save(product)` — JPA treats this as an update when `id` is non-null.

Delete (Remove a product):
- User action: access `/products/delete/{id}` (triggered by a link or button).
- Controller: `ProductController#deleteProduct` calls `productService.deleteProduct(id)`.
- Service: `ProductServiceImpl#deleteProduct` calls `productRepository.deleteById(id)` to remove the entity.

Search (helper read operation):
- Controller: `ProductController#searchProducts` receives `keyword` param and calls `productService.searchProducts(keyword)`.
- Service: `ProductServiceImpl#searchProducts` uses `product_repository.findByNameContaining(keyword)` to return matches.

**Key files & locations**
- `src/main/java/com/example/productmanagement/controller/ProductController.java` — web endpoints and UI flow.
- `src/main/java/com/example/productmanagement/controller/DashboardController.java` — statistics dashboard controller.
- `src/main/java/com/example/productmanagement/service/ProductService.java` — service interface.
- `src/main/java/com/example/productmanagement/service/ProductServiceImpl.java` — business logic (calls repository).
- `src/main/java/com/example/productmanagement/repository/ProductRepository.java` — Spring Data JPA repository (extends `JpaRepository<Product, Long>`).
- `src/main/java/com/example/productmanagement/entity/Product.java` — JPA entity mapping with validation annotations.
- `src/main/resources/templates/product-list.html` — Thymeleaf template for list with sorting, filtering, and pagination.
- `src/main/resources/templates/product-form.html` — Thymeleaf template for form with validation error display.
- `src/main/resources/templates/dashboard.html` — Statistics dashboard view.

**Homework Features Implemented (Part B)**

Exercise 5: Advanced Search (12 points) ✅
- Multi-criteria search (name, category, price range)
- Category filter dropdown with all unique categories
- Search with pagination support

Exercise 6: Validation (10 points) ✅
- Validation annotations on Product entity (@NotBlank, @Size, @Pattern, @DecimalMin, @DecimalMax, @Min)
- Validation in controller with @Valid and BindingResult
- Validation error display in product-form.html with error styling

Exercise 7: Sorting & Filtering (10 points) ✅
- Sorting by all columns (id, code, name, price, quantity, category) in ascending/descending order
- Category filter with quick filter buttons
- Combined sorting and filtering in one interface

Exercise 8: Statistics Dashboard (8 points) ✅
- Dashboard showing total products count, total inventory value, average price
- Products by category with counts
- Low stock alerts (quantity < 10)
- Recent products (last 5 added)
- Accessible at http://localhost:8080/dashboard

If you want, I can:
- Start the app now and show the console output.
- Run the test suite and fix any failing tests.
- Remove the temporary `CommandLineRunner` used for debugging in `ProductManagementApplication.java`.

---
Generated by your assistant to help run and understand the app.
