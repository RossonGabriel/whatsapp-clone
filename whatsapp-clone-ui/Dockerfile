# Development stage
FROM node:22-alpine
WORKDIR /app
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application
COPY . .

# Create a browser-compatible version of stompjs
RUN sed -i 's/require('\''net'\'')/null/g' node_modules/stompjs/lib/stomp-node.js && \
    sed -i 's/require('\''tls'\'')/null/g' node_modules/stompjs/lib/stomp-node.js && \
    sed -i 's/require('\''fs'\'')/null/g' node_modules/stompjs/lib/stomp-node.js

# Expose port for development server
EXPOSE 4200

# Start the development server
CMD ["npm", "start", "--", "--host", "0.0.0.0"] 