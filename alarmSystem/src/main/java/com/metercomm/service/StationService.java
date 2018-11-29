package com.metercomm.service;

import com.metercomm.mapper.StationMapper;
import com.metercomm.model.db.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor=Exception.class)
public class StationService {

    @Autowired
    private StationMapper stationMapper;

    public List<Station> getAllStations() {
        return stationMapper.selectAll();
    }

    public Station findStationByName(String stationName) {
        return stationMapper.selectByPrimaryKey(stationName);
    }

}