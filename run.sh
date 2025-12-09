#!/bin/bash

# FirstClub Membership Program - Run Script
# This script helps you build and run the application easily

set -e  # Exit on error

echo "╔════════════════════════════════════════════════════════════╗"
echo "║       FirstClub Membership Program                         ║"
echo "║       Spring Boot Application Runner                       ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check for Java
echo "🔍 Checking Java installation..."
if ! command_exists java; then
    echo "❌ Java is not installed!"
    echo "   Please install Java 17 or higher"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java version is too old (found: Java $JAVA_VERSION)"
    echo "   Please install Java 17 or higher"
    exit 1
fi

echo "✅ Java version: $(java -version 2>&1 | head -n 1)"
echo ""

# Check for Maven
echo "🔍 Checking Maven installation..."
if command_exists mvn; then
    MAVEN_CMD="mvn"
    echo "✅ Maven found: $(mvn --version | head -n 1)"
elif [ -f "./mvnw" ]; then
    MAVEN_CMD="./mvnw"
    echo "✅ Using Maven Wrapper"
else
    echo "❌ Neither Maven nor Maven Wrapper found!"
    echo "   Please install Maven or ensure mvnw is present"
    exit 1
fi
echo ""

# Parse command line arguments
ACTION=${1:-run}

case $ACTION in
    build)
        echo "🔨 Building the application..."
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        $MAVEN_CMD clean install
        echo ""
        echo "✅ Build completed successfully!"
        ;;

    run)
        echo "🚀 Starting the application..."
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo "   The application will start on: http://localhost:8080"
        echo "   H2 Console will be available at: http://localhost:8080/h2-console"
        echo ""
        echo "   Press Ctrl+C to stop"
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo ""
        $MAVEN_CMD spring-boot:run
        ;;

    clean)
        echo "🧹 Cleaning the project..."
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        $MAVEN_CMD clean
        echo ""
        echo "✅ Project cleaned successfully!"
        ;;

    test)
        echo "🧪 Running tests..."
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        $MAVEN_CMD test
        ;;

    help|--help|-h)
        echo "Usage: ./run.sh [command]"
        echo ""
        echo "Commands:"
        echo "  build    - Build the application (mvn clean install)"
        echo "  run      - Build and run the application (default)"
        echo "  clean    - Clean build artifacts"
        echo "  test     - Run tests"
        echo "  help     - Show this help message"
        echo ""
        echo "Examples:"
        echo "  ./run.sh           # Build and run"
        echo "  ./run.sh build     # Just build"
        echo "  ./run.sh clean     # Clean project"
        echo ""
        ;;

    *)
        echo "❌ Unknown command: $ACTION"
        echo "   Run './run.sh help' for usage information"
        exit 1
        ;;
esac
