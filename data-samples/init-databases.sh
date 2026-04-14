#!/bin/sh
set -e

echo "🚀 Checking databases..."

# Функция для проверки и создания базы, если её нет
create_db_if_not_exists() {
  DB_NAME=$1
  echo "Checking if database '$DB_NAME' exists..."
  RESULT=$(psql -U "$POSTGRES_USER" -tAc "SELECT 1 FROM pg_database WHERE datname='${DB_NAME}'")
  if [ "$RESULT" != "1" ]; then
    echo "Creating database '$DB_NAME'..."
    psql -U "$POSTGRES_USER" -c "CREATE DATABASE \"$DB_NAME\";"
  else
    echo "Database '$DB_NAME' already exists. Skipping."
  fi
}

# Список баз для микросервисов
create_db_if_not_exists "user_service"
create_db_if_not_exists "shop_service"

echo "✅ All databases ready!"
