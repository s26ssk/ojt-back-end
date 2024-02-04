package com.ra.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DistanceMatrixResponse {
    private String authenticationResultCode;
    private List<ResourceSet> resourceSets;
    private int statusCode;
    private String statusDescription;
    private String traceId;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResourceSet {
        private int estimatedTotal;
        private List<Resource> resources;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Resource {
        private List<Location> origins;
        private List<Location> destinations;
        private List<Result> results;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Location {
        private double latitude;
        private double longitude;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        private int destinationIndex;
        private int originIndex;
        private double travelDistance;
        private double travelDuration;
    }
}
