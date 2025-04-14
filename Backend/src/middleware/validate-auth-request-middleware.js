const { StatusCodes } = require('http-status-codes');
const { SignUpService } = require('../services');
const { ErrorResponse } = require("../utils/common");
const AppError = require('../utils/errors/app-error');

function validateAuthRequest(req, res, next) {
    if (!req.body.email) {
        ErrorResponse.error = new AppError(['Email was not found in the incoming request in the correct form'], StatusCodes.BAD_REQUEST);
        return res
            .status(StatusCodes.BAD_REQUEST)
            .json(ErrorResponse);
    }
    if (!req.body.password) {
        ErrorResponse.error = new AppError(['password was not found in the incoming request in the correct form'], StatusCodes.BAD_REQUEST);
        return res
            .status(StatusCodes.BAD_REQUEST)
            .json(ErrorResponse); 
    }
    next();
}
async function checkAuth(req, res, next) {
    try {
        
        
        const authHeader = req.headers.authorization; // Fetch the Authorization header
        if (!authHeader) {
            ErrorResponse.error = new AppError('No Authorization header provided', StatusCodes.BAD_REQUEST);
            return res.status(StatusCodes.BAD_REQUEST).json(ErrorResponse);
        }
        const token = authHeader.split(' ')[1]; // Extract the token part after 'Bearer'
        if (!token) {
            ErrorResponse.error = new AppError('Malformed Authorization header', StatusCodes.BAD_REQUEST);
            return res.status(StatusCodes.BAD_REQUEST).json(ErrorResponse);
        }
        const response = await SignUpService.isAuthentication(token);
        console.log("the response will be in auth....", response)
        if (response) {
            req.user = response;
            next()
        }
    }
    catch (error) {
    
        return res
            .status(error.statusCode)
            .json(error);
    }
}


module.exports = { validateAuthRequest, checkAuth }