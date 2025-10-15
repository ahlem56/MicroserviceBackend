FROM php:8.3-fpm

# Install system dependencies
RUN apt-get update \
 && apt-get install -y git unzip libpq-dev \
 && docker-php-ext-install pdo pdo_pgsql

# Copy composer binary
COPY --from=composer:2 /usr/bin/composer /usr/bin/composer

# Set working directory
WORKDIR /var/www/html

# Copy the entire project into the container
COPY . .

# Install dependencies inside the app folder
WORKDIR /var/www/html/app
RUN composer install --no-interaction --no-scripts --prefer-dist || true

# Expose the port
EXPOSE 8084

# Start Symfony built-in server
CMD ["php", "-S", "0.0.0.0:8084", "-t", "public"]
