const CrudRepositery = require('./crud-repository');
const { Parking } = require('../models')

class ParkingRepository extends CrudRepositery {
    constructor() {
        super(Parking);
    }
    
     toRadians(degrees) {
        return degrees * Math.PI / 180;
    }
    
    calculateDistance(lat1, lon1, lat2, lon2) {
        const earthRadius = 6371000; // in meters
        const dLat = this.toRadians(lat2 - lat1);
        const dLon = this.toRadians(lon2 - lon1);
        const a = Math.sin(dLat / 2) ** 2 +
                  Math.cos(this.toRadians(lat1)) * Math.cos(this.toRadians(lat2)) *
                  Math.sin(dLon / 2) ** 2;
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }
    
    async getParking(data) {
        try {
            const { coordinates} = data;
            
            
            const allParkings = await Parking.find();
    
            const userLat = parseFloat(coordinates.latitude);
            const userLon = parseFloat(coordinates.longitude);
            const radius = 10000; // meters
    
            const filteredParkings = allParkings.filter(parking => {
                const parkLat = parseFloat(parking.coordinates.latitude);
                const parkLon = parseFloat(parking.coordinates.longitude);
                console.log(`User: ${userLat}, ${userLon} | Parking: ${parkLat}, ${parkLon}`);
                
                const distance = this.calculateDistance(userLat, userLon, parkLat, parkLon);
               console.log(`Distance: ${distance} meters`);
               
    
                // Optional: You can also filter by startDate and startTime if needed
                return distance <= radius &&this.isWithinSlot(data.startDate, data.startTime, parking);

            });
    
            return filteredParkings;
        } catch (error) {
            throw error;
        }
    }
     parseDateTime(dateStr, timeStr) {
        const [day, month, year] = dateStr.split('-');
        return new Date(`${year}-${month}-${day}T${timeStr}:00`);
    }
    
     isWithinSlot(userDate, userTime, parking) {
        const userDateTime = this.parseDateTime(userDate, userTime);
        const startSlot = this.parseDateTime(parking.startDate, parking.startTime);
        const endSlot = this.parseDateTime(parking.endDate, parking.endTime);
    
        return userDateTime >= startSlot && userDateTime <= endSlot;
    }
    
}



module.exports = ParkingRepository;