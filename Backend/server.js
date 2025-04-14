const dotenv=require('dotenv');
dotenv.config();
const express = require('express')
const apiroutes=require('./src/routes')
const {ServerConfig,dbConfig}=require('./src/config');
const app = express();
const mongoose = require('mongoose');

// Connect to MongoDB
mongoose.connect(dbConfig.MONGODB_URI, dbConfig.options)
    .then(() => console.log('MongoDB connected successfully'))
    .catch(err => {
        console.error('MongoDB connection error:', err);
        process.exit(1);
    });


// Middleware for parsing JSON bodies
app.use(express.json());

// API routes 
app.use("/api", apiroutes);

// Basic health check endpoint
app.get('/', (req, res) => {
    res.status(200).json({
        status: 'success',
        message: 'Server is running'
    });
});

// Start server
const PORT = ServerConfig.BACKEND_PORT ;
app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
    console.log(`Environment: ${process.env.NODE_ENV || 'development'}`);
}); 