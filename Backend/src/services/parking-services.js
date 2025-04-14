const AppError = require("../utils/errors/app-error");
const { StatusCodes } = require("http-status-codes");
const { ParkingRepository } = require("../repositories");

const parkingRepo= new ParkingRepository();



const addParking=async (data)=>{
    try {
        const response=await parkingRepo.create(data);
        return response;
    } catch (error) {
        console.log(error);
        throw new AppError(error.message,StatusCodes.INTERNAL_SERVER_ERROR);
        
    }
}
async function getParking(data){
    try {
        const response=await parkingRepo.getParking(data);
        return response;
    } catch (error) {
        console.log(error);
        throw new AppError(error.message,StatusCodes.INTERNAL_SERVER_ERROR);
        
    }
}


module.exports={
    addParking,
    getParking
}