const {SuccessResponse, ErrorResponse} = require('../utils/common');
const {StatusCodes} = require('http-status-codes');
const {ParkingService}=require('../services');


const addParking=async (req,res)=>{
    try {
        console.log(req);
        
        const {street,city,coordinates,dimensions,startTime,startDate,endDate,endTime,pincode } = req.body;
        user=req.user._id;

        const data={
            user,
            street,
            city,
            pincode,
            coordinates,
            dimensions,
            startDate,
            startTime,
            endDate,
            endTime
        };
        console.log("data:",data);
        const response=await ParkingService.addParking(data);
        console.log("response:",response);
        SuccessResponse.data=response;
        return res.status(StatusCodes.ACCEPTED).json(SuccessResponse);
    } catch (error) {
        console.log(error);
        ErrorResponse.error=error;
        return res.status(StatusCodes).json(ErrorResponse);
    }
} 
async function getParking(req,res){
    try {
        console.log(req.body);
        
        const {latitude,longitude,startDate,startTime} = req.body;
        const coordinates={
            latitude,
            longitude
        }
        const data={
            coordinates,
            startDate,
            startTime,
        }
        const response=await ParkingService.getParking(data);
        SuccessResponse.data=response;
        return res.status(StatusCodes.ACCEPTED).json(SuccessResponse);
    } catch (error) {
        console.log(error);
        ErrorResponse.error=error;
        return res.status(StatusCodes).json(ErrorResponse);
    }
}


module.exports={
    addParking,
    getParking
}