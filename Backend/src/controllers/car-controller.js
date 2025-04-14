const { SuccessResponse, ErrorResponse } = require('../utils/common');
const { StatusCodes } = require('http-status-codes');
const { CarService } = require('../services');



const getAllCars = async (req, res) => {
    try {
        
        const cars = await CarService.getAllCars(req.query);
        SuccessResponse.data = cars;
        return res.status(StatusCodes.OK).json(SuccessResponse);

    } catch (error) {
        console.log(error.message);
        ErrorResponse.error = error.message;
        return res.status(error.StatusCodes).json(ErrorResponse);

    }
}
const addCar = async (req, res) => {
    try {

        const { companyName, model, vehicleNumber,type } = req.body;
        // Check if all details are provided
        if (!companyName || !model || !vehicleNumber||!type) {

            ErrorResponse.error = 'Please provide all details';
            return res.status(StatusCodes.BAD_REQUEST).json(ErrorResponse);


        }
        const car = await CarService.addCar({companyName, model, vehicleNumber,type},req.user );
        SuccessResponse.data = car;
        return res.status(StatusCodes.CREATED).json(SuccessResponse);
    } catch (error) {
        console.log(error.message);
        ErrorResponse.error = error.message;
        return res.status(error.StatusCodes).json(ErrorResponse);
    }
}



module.exports = {
    
    getAllCars,
    addCar,
    
};