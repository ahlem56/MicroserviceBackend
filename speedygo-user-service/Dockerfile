#############################################
# Build stage - install dependencies with Composer
#############################################
FROM composer:2 AS build

WORKDIR /app

ENV APP_ENV=prod APP_DEBUG=0 COMPOSER_ALLOW_SUPERUSER=1

# Copy composer manifests and install vendor deps first (better layer caching)
COPY app/composer.json app/composer.lock ./
RUN composer install \
    --no-dev \
    --prefer-dist \
    --no-interaction \
    --no-progress \
    --no-scripts \
    --ignore-platform-req=php

# Copy the full Symfony application
COPY app/ ./

# Ensure optimized autoloader once sources are available (skip scripts to avoid cache clear)
RUN composer install \
    --no-dev \
    --prefer-dist \
    --no-interaction \
    --no-progress \
    --optimize-autoloader \
    --no-scripts \
    --ignore-platform-req=php

#############################################
# Runtime stage - PHP Apache
#############################################
FROM php:8.3-apache AS runtime

# Install PHP extensions needed by Symfony (pgsql, intl, etc.)
RUN apt-get update && apt-get install -y \
        libpq-dev \
        libicu-dev \
        libzip-dev \
    && docker-php-ext-install \
        pdo \
        pdo_pgsql \
        intl \
        zip \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /var/www/html

# Copy application from build stage
COPY --from=build /app /var/www/html

# Configure Apache to serve Symfony from /app/public on port 8084
ENV APACHE_DOCUMENT_ROOT=/var/www/html/public

RUN sed -ri 's!/var/www/html!${APACHE_DOCUMENT_ROOT}!g' /etc/apache2/sites-available/*.conf \
    && sed -ri 's!/var/www/!${APACHE_DOCUMENT_ROOT}/!g' /etc/apache2/apache2.conf \
    && a2enmod rewrite \
    && { \
        echo "Listen 8084"; \
        echo "<VirtualHost *:8084>"; \
        echo "    DocumentRoot ${APACHE_DOCUMENT_ROOT}"; \
        echo "    <Directory ${APACHE_DOCUMENT_ROOT}>"; \
        echo "        AllowOverride All"; \
        echo "        Require all granted"; \
        echo "    </Directory>"; \
        echo "    ErrorLog /var/log/apache2/error.log"; \
        echo "    CustomLog /var/log/apache2/access.log combined"; \
        echo "</VirtualHost>"; \
    } > /etc/apache2/sites-available/000-default.conf

# Symfony front-controller rewrite rules
RUN cat <<'HTACCESS' > ${APACHE_DOCUMENT_ROOT}/.htaccess
<IfModule mod_negotiation.c>
    Options -MultiViews
</IfModule>

<IfModule mod_rewrite.c>
    RewriteEngine On

    RewriteCond %{REQUEST_FILENAME} -f
    RewriteRule ^ - [L]

    RewriteRule ^ index.php [L]
</IfModule>
HTACCESS

EXPOSE 8084

# Symfony needs APP_ENV/APP_DEBUG; allow override via docker compose
ENV APP_ENV=prod \
    APP_DEBUG=0

CMD ["apache2-foreground"]
