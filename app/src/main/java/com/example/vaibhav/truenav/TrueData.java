package com.example.vaibhav.truenav;

//handles all the business logic

public class TrueData {

    enum DirectionType {
        WALKING,
        TRANSIT,
        DRIVING;
    }

    private final int OP_COST = 60;
    private final int GAS_COST = 260;
    private final int CAR_EMISSIONS_COST = 411; //note: this is in grams
    private final int TRANSIT_EMISSIONS_COST = 105; //note: this is in grams
    private final String TRANSIT_COST = "2.75";

    int mGasCost = 0;
    int mOpCost = 0;
    String distance;
    String duration;
    String traffic;
    Boolean givenTraffic;
    DirectionType directionType;

    public TrueData(String dist, String dur, String traf, Boolean givenTraffic, DirectionType directionType) {
        this.distance = dist;
        this.duration = dur;
        this.traffic = traf;
        this.givenTraffic = givenTraffic;
        this.directionType = directionType;
    }

    public void cleanData() {
        //handles distance
        int i = 0;
        while (distance.charAt(i) != ' ') {
            ++i;
        }
        distance = distance.substring(0, i);

        //handles traffic if that's given
        if (givenTraffic) {
            i = 0;
            while (traffic.charAt(i) != ' ') {
                ++i;
            }
            traffic = traffic.substring(0, i);
        }
    }

    public int calculateCost() {
        double dist = Double.parseDouble(distance);
        double opCost = dist * OP_COST;
        double gasCost = dist * GAS_COST;
        float totalCost = (float) (opCost + gasCost);
        totalCost = totalCost / 100;
        int tCost = Math.round(totalCost);
        mGasCost = Math.round((float) gasCost / 100);
        mOpCost = tCost - mGasCost;
        return tCost;
    }

    public int calculateEmissions() {
        double dist = Double.parseDouble(distance);
        float emissions = 0;
        if (givenTraffic) {
            emissions = (float) (dist * CAR_EMISSIONS_COST);
        } else {
            emissions = (float) (dist * TRANSIT_EMISSIONS_COST);
        }
        emissions = emissions / 1000;
        return Math.round(emissions);
    }

    public String createTimeText() {
        String response = duration;
        switch (directionType) {
            case WALKING:
                response = response + " (" + distance + " miles) to your destination";
                break;
            case TRANSIT:
                response = response + " (" + distance + " miles) with no delays";
                break;
            case DRIVING:
                String durationCopy = duration;
                int j = 0;
                while (durationCopy.charAt(j) != ' ') {
                    j++;
                }
                durationCopy = durationCopy.substring(0, j);
                int duration = Integer.valueOf(durationCopy);
                int trafficTime = Integer.valueOf(traffic);
                int travelTime = trafficTime - duration;
                String trafficStatus = "";
                if (givenTraffic) {
                    if (travelTime <= 0) {
                        trafficStatus = "no";
                    } else if (travelTime > 0 && travelTime <= 5) {
                        trafficStatus = "light";
                    } else if (travelTime > 5 && travelTime <= 15) {
                        trafficStatus = "moderate";
                    } else if (travelTime > 15) {
                        trafficStatus = "heavy";
                    }
                }
                response = response + " (" + distance + " miles) with " + trafficStatus + " traffic";
                break;
            default:
                break;
        }
        return response;
    }

    public String createCostText() {

        String response = "Expected cost is ";
        switch (directionType) {
            case WALKING:
                response = response + "$0!!";
                break;
            case TRANSIT:
                response = response + "$" + TRANSIT_COST;
                break;
            case DRIVING:
                int totalCost = calculateCost();
                response = response + "approximately $" + totalCost + ", with gas costing $" + mGasCost +
                        " and $" + mOpCost + " for miscellaneous costs";
                break;
            default:
                break;
        }
        return response;
    }

    public String createEmissionsText() {

        String response = "";
        int emissionsAmount = 0;
        switch (directionType) {
            case WALKING:
                response = "Walking will release 0 kg of CO2 into the atmosphere";
                break;
            case TRANSIT:
                emissionsAmount = calculateEmissions();
                response = "Taking public transit will release " + emissionsAmount + " kg of CO2 into the atmosphere";
                break;
            case DRIVING:
                emissionsAmount = calculateEmissions();
                response = "Taking public transit will release " + emissionsAmount + " kg of CO2 into the atmosphere";
                break;
            default:
                break;
        }

        return response;

    }
}


