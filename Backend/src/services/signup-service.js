const { SignUpRepository } = require("../repositories");
const AppError = require("../utils/errors/app-error");
const { StatusCodes } = require("http-status-codes");
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const { ServerConfig } = require("../config");
const serverConfig = require("../config/server-config");

const SignUpRepo = new SignUpRepository();
async function signUp(data) {
    try {
        const user = await SignUpRepo.create(data);
        const token = jwt.sign({ userId: user._id }, ServerConfig.JWT_SECRET, { expiresIn: ServerConfig.EXPIRES_IN });
        return {user,token};
    } catch (error) {
        console.log(error);
        if (error.name === 'MongoServerError' && error.code === 11000) {

            throw new AppError('User already exists', StatusCodes.CONFLICT);
        }
        throw new AppError(
            "Not able to create the resource",
            StatusCodes.INTERNAL_SERVER_ERROR
        );
    }
}

async function signIn(data) {
    try {
        const user = await SignUpRepo.findByEmail(data.email);
        if (!user) {
            throw new AppError("User not found", StatusCodes.NOT_FOUND);
        }
        const isPasswordCorrect = await bcrypt.compare(data.password, user.password);
        if (!isPasswordCorrect) {
            throw new AppError("Invalid password", StatusCodes.UNAUTHORIZED);
        }

        const token = jwt.sign({ userId: user._id }, ServerConfig.JWT_SECRET, { expiresIn: ServerConfig.EXPIRES_IN });
        return { user, token };
    } catch (error) {
        throw new AppError(error.message, StatusCodes.INTERNAL_SERVER_ERROR);
    }
}

async function isAuthentication(token) {
    try {
        if (!token) {
            throw new AppError('Missing JWT token', StatusCodes.BAD_REQUEST);
        }
        const decoded = jwt.verify(token, serverConfig.JWT_SECRET);
        const user = await SignUpRepo.get(decoded.userId);
        if (!user) {
            throw new AppError('Invalid user token', StatusCodes.UNAUTHORIZED);
        }
        return user;
    } catch (error) {
        
        if (error instanceof AppError) throw error;
        if (error.name === 'JsonWebTokenError') {
            throw new AppError('Invalid JWT token', StatusCodes.BAD_REQUEST);
        }
        if (error.name === 'TokenExpiredError') {
            throw new AppError('JWT token expired', StatusCodes.UNAUTHORIZED);
        }
        throw new AppError("Something went wrong", StatusCodes.INTERNAL_SERVER_ERROR);
    }
}
const resetPassword = async (email, password) => {
    const user = await SignUpRepo.findByEmail(email);
    if (!user) {
        throw new AppError("User not found", StatusCodes.NOT_FOUND);
    }
    const isPasswordSame = await bcrypt.compare(password, user.password);
    if (isPasswordSame) {
        throw new AppError("New password cannot be the same as the old password", StatusCodes.BAD_REQUEST);
    }
    user.password = password;

    await user.save();
    return user;
}
module.exports = {
    signUp,
    signIn,
    isAuthentication,
    resetPassword
}