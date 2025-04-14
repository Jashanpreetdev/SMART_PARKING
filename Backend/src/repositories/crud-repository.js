const { StatusCodes } = require("http-status-codes");
const AppError = require("../utils/errors/app-error");

class CrudRepository {
    constructor(model) {
        this.model = model;
    }
    async create(data) {
        try {
            const response = await this.model.create(data);
            return response;
        } catch (error) {
            console.log(error);
            
            if(error.name == 'MongoServerError') throw error;
            throw new AppError(error.message, StatusCodes.INTERNAL_SERVER_ERROR);
        }
    } 

    async destroy(id) {
        try {
            const response = await this.model.findByIdAndDelete(id);
            if(!response) {
                throw new AppError(
                    'Not able to find the resource',
                    StatusCodes.NOT_FOUND
                );
            }
            return response;
        } catch (error) {
            if(error.name == 'MongoServerError') throw error;
            throw new AppError(
                'Something went wrong while deleting resource',
                StatusCodes.INTERNAL_SERVER_ERROR
            );
        }
    }

    async get(id) {
        try {
            const response = await this.model.findById(id);
            if(!response) {
                throw new AppError(
                    'Not able to find the resource',
                    StatusCodes.NOT_FOUND
                );
            }
            return response;
        } catch (error) {
            if(error.name == 'AppError') throw error;
            throw new AppError(
                'Something went wrong while getting resource',
                StatusCodes.INTERNAL_SERVER_ERROR
            );
        }
    }

    async getAll(query={}) {
        try {
            const response = await this.model.find(query)
            // .sort({createdAt:-1});
            return response;
        } catch (error) {
            throw new AppError(
                'Something went wrong while getting all resources',
                StatusCodes.INTERNAL_SERVER_ERROR
            );
        }
    }

    async update(id, data) {
        try {
            const response = await this.model.findByIdAndUpdate(id, data, {new: true});
            if(!response) {
                throw new AppError(
                    'Not able to find the resource',
                    StatusCodes.NOT_FOUND
                );
            }
            return response;
        } catch (error) {
            if(error.name == 'AppError') throw error;
            throw new AppError(
                'Something went wrong while updating resource',
                StatusCodes.INTERNAL_SERVER_ERROR
            );
        }
    }
}

module.exports = CrudRepository;
