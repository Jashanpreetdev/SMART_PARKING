const { SuccessResponse, ErrorResponse } = require("../utils/common");
const { SignUpService } = require("../services");
const { StatusCodes } = require("http-status-codes");
const { SignUpRepository } = require("../repositories");
const  AppError  = require("../utils/errors/app-error");
const { OTP } = require("../models");
const { response } = require("express");

const signUpRepository = new SignUpRepository();
const signUp = async (req, res) => {
    console.log(req.body);
    
    try {
        const { name, email, password, phone, otp } = req.body;
        // Check if all details are provided
        if (!name || !email || !password || !otp) {
            ErrorResponse.error = new AppError('All fields are required', StatusCodes.BAD_REQUEST);
            return res.status(StatusCodes.BAD_REQUEST).json(ErrorResponse);
        }
        const existingUser = await signUpRepository.findByEmail(email);
        if (existingUser) {
            console.log("ye");
            
            ErrorResponse.error = new AppError('User already exists', StatusCodes.BAD_REQUEST);
            return res.status(StatusCodes.BAD_REQUEST).json(ErrorResponse);
        }
        const responseotp = await OTP.find({email}).sort({createdAt:-1}).limit(1);
        
        if (responseotp.length === 0 || otp !== responseotp[0].otp) {
            ErrorResponse.error = new AppError('The OTP is not valid', StatusCodes.BAD_REQUEST);
            return res.status(StatusCodes.BAD_REQUEST).json(ErrorResponse);
        }
        const response = await SignUpService.signUp({
            name,
            email,
            password,
            otp,phone
        });
        SuccessResponse.data = response;
        return res.status(StatusCodes.CREATED).json(SuccessResponse);
    } catch (error) {
        console.log(error);

        ErrorResponse.error = error;
        return res.status(error.statusCode).json(ErrorResponse);
    }
}

const signIn = async (req, res) => {
    try {
        const response = await SignUpService.signIn(req.body);
        SuccessResponse.data = response;
        return res.status(StatusCodes.CREATED).json(SuccessResponse);
    } catch (error) {
        ErrorResponse.error = error;
        return res.status(StatusCodes.INTERNAL_SERVER_ERROR).json(ErrorResponse);
    }
}
const resetPassword = async (req, res) => {
    try {
        const {  email, password, otp } = req.body;
        // Check if all details are provided
        if (!email || !password || !otp) {
            ErrorResponse.error = new AppError('All fields are required', StatusCodes.BAD_REQUEST);
            return res.status(StatusCodes.BAD_REQUEST).json(ErrorResponse);
        }
        const existingUser = await signUpRepository.findByEmail(email);
        if (!existingUser) {
            console.log("ye");
            ErrorResponse.error = new AppError('User not found', StatusCodes.NOT_FOUND);
            return res.status(StatusCodes.NOT_FOUND).json(ErrorResponse);
        }
        const responseotp = await OTP.find({email}).sort({createdAt:-1}).limit(1);
        if (responseotp.length === 0 || otp !== responseotp[0].otp) {
            ErrorResponse.error = new AppError('The OTP is not valid', StatusCodes.BAD_REQUEST);
            return res.status(StatusCodes.BAD_REQUEST).json(ErrorResponse);
        }
        const response = await SignUpService.resetPassword(email, password);
        SuccessResponse.data = response;
        return res.status(StatusCodes.OK).json(SuccessResponse);
    } catch (error) {
        ErrorResponse.error = error;
        return res.status(error.statusCode).json(ErrorResponse);
    }
}
const check = async (req, res) => {
    try {

        const response = req.user; 
        SuccessResponse.data = response;
        return res.status(StatusCodes.CREATED).json(SuccessResponse);
    } catch (error) {
        ErrorResponse.error = error;
        return res.status(StatusCodes.INTERNAL_SERVER_ERROR).json(ErrorResponse);
    }
}
module.exports = {
    signUp,
    signIn,
    resetPassword,check
}
