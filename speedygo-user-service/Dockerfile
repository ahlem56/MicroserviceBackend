FROM php:8.3-fpm

# system deps
RUN apt-get update \
 && apt-get install -y git unzip libpq-dev \
 && docker-php-ext-install pdo pdo_pgsql

# composer
COPY --from=composer:2 /usr/bin/composer /usr/bin/composer

WORKDIR /var/www/html

CMD ["php-fpm"]
