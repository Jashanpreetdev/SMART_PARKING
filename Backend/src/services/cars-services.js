const AppError = require("../utils/errors/app-error");
const { StatusCodes } = require("http-status-codes");
const { CarRepository } = require("../repositories");
// const user = require("../models/user");

const CarRepo= new CarRepository();

const getAllCars = async (filter={}) => {
  try {
    const cars = await CarRepo.getAll(filter);
    return cars;
  } catch (error) {
    throw new AppError(error.message, StatusCodes.INTERNAL_SERVER_ERROR);
  }
}

const addCar= async (data,user) => {
    try {
        const car = await CarRepo.create({...data,user});
        return car;
    } catch (error) {
        throw new AppError(error.message, StatusCodes.INTERNAL_SERVER_ERROR);
    }
}


const userCar=async(user)=>{
  try {
    const response=await CarRepo.getCarByUser(user._id);
  } catch (error) {
    console.log(error);
    throw new AppError(error.message,StatusCodes.INTERNAL_SERVER_ERROR);
  }
}

// const getAllBrands = async () => {
//     try {
//         const brands = await CarRepo.getAllBrands();
//         return brands;
//     } catch (error) {
//         throw new AppError(error.message, StatusCodes.INTERNAL_SERVER_ERROR);
//     }
// }

// const getAllModelsByBrandId = async (id) => {
//     try {
//         const models = await CarRepo.getAllModelsByBrandId(id);
//         return models;
//     } catch (error) {
//         throw new AppError(error.message, StatusCodes.INTERNAL_SERVER_ERROR);
//     }
// }
module.exports = {
    getAllCars,
    addCar,
    userCar,
    // getAllBrands,
    // getAllModelsByBrandId
}