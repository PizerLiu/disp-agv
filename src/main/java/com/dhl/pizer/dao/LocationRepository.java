package com.dhl.pizer.dao;

import java.util.List;
import com.dhl.pizer.entity.Location;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends MongoRepository<Location, String> {

    Location findByLocation(String location);

    List<Location> findByType(String type);

    List<Location> findAllByLocation(String location);

}
