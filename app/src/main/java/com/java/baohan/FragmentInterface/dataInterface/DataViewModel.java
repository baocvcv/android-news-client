package com.java.baohan.FragmentInterface.dataInterface;

import androidx.lifecycle.ViewModel;

import com.java.baohan.backend.DataEntry;
import com.java.baohan.backend.PandemicData;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DataViewModel extends ViewModel {

    private ConcurrentHashMap<String, List<DataEntry>> countryData;
    private ConcurrentHashMap<String, List<DataEntry>> provinceData;
    private ConcurrentHashMap<String, ConcurrentHashMap<String, List<DataEntry>>> provinceDetails;

    public List<DataEntry> chinaTimeSeries;
    public List<DataEntry> worldTimeSeries;
    public LinkedHashMap<String, DataEntry> keyCountrySnapshot;
    public LinkedHashMap<String, DataEntry> provinceDataSnapshot;
    public Map<String, LinkedHashMap<String, DataEntry>> provinceDetailDataSnaphot;
    public boolean dataReady;

    public static final String[] STAT_ENTRIES = new String[]{"curInfected", "totalInfected", "totalCured", "totalDead"};

    public DataViewModel() {
        countryData = PandemicData.getCountryData();
        provinceData = PandemicData.getProvinceData();
        provinceDetails = PandemicData.getProvinceDetails();

        if(!PandemicData.isDataReady()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!PandemicData.isDataReady()) {
                        try {
                            Thread.sleep(10);
                        } catch (Exception e) {}
                    }
                    buildData();
                }
            }).start();
        }
    }

    private void buildData() {
        dataReady = false;
        chinaTimeSeries = countryData.get("China");

        HashMap<LocalDate, DataEntry> tmpWorldSeries = new HashMap<>();
        HashMap<String, DataEntry> tmpWorldSnapshot = new HashMap<>();
        for(Map.Entry<String, List<DataEntry>> e: countryData.entrySet()) {
            String country = e.getKey();
            List<DataEntry> series = e.getValue();
            if(!country.equals("China") && !country.equals("World")) {
                if(country.equals("United States of America"))
                    tmpWorldSnapshot.put("U.S.A.", series.get(series.size() - 1));
                else
                    tmpWorldSnapshot.put(country, series.get(series.size() - 1));
            }
            for(DataEntry en: series) {
                tmpWorldSeries.putIfAbsent(en.date, new DataEntry(0, 0, 0, en.date));
                tmpWorldSeries.get(en.date).accumulate(en);
            }
        }
        worldTimeSeries = tmpWorldSeries.entrySet()
                .stream()
                .sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        keyCountrySnapshot = tmpWorldSnapshot.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().confirmed - e1.getValue().confirmed)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        provinceDataSnapshot = new LinkedHashMap<>();
        provinceDetailDataSnaphot = new LinkedHashMap<>();
        for(String prov: provinceData.keySet()) {
            List<DataEntry> series = provinceData.get(prov);
            provinceDataSnapshot.put(prov, series.get(series.size() - 1));

            if(provinceDetails.containsKey(prov)) {
                HashMap<String, DataEntry> tmp = new HashMap<>();
                for (Map.Entry<String, List<DataEntry>> e : provinceDetails.get(prov).entrySet()) {
                    series = e.getValue();
                    tmp.put(e.getKey(), series.get(series.size() - 1));
                }
                provinceDetailDataSnaphot.put(
                        prov,
                        tmp.entrySet()
                        .stream()
                        .sorted((e1, e2) -> e1.getValue().confirmed - e2.getValue().confirmed)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new))
                );
            }
        }
        provinceDataSnapshot = provinceDataSnapshot.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().confirmed - e1.getValue().confirmed)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        dataReady = true;
    }

//    public static List<> getChinaTimeSeriesData() {
//    }

    public Map<String, Map<String, Integer>> getChinaStats() {
        if(!dataReady) {
            return null;
        }

        DataEntry today = chinaTimeSeries.get(chinaTimeSeries.size() - 1);
        DataEntry yesterday = chinaTimeSeries.get(chinaTimeSeries.size() - 2);

        HashMap<String, Integer> cumulative = new HashMap<>();
        int curInfected = today.confirmed - today.cured - today.dead;
        cumulative.put(STAT_ENTRIES[0], curInfected);
        cumulative.put(STAT_ENTRIES[1], today.confirmed);
        cumulative.put(STAT_ENTRIES[2], today.cured);
        cumulative.put(STAT_ENTRIES[3], today.dead);

        HashMap<String, Integer> change = new HashMap<>();
        change.put(STAT_ENTRIES[0], curInfected - (yesterday.confirmed - yesterday.cured - yesterday.dead));
        change.put(STAT_ENTRIES[1], today.confirmed - yesterday.confirmed);
        change.put(STAT_ENTRIES[2], today.cured - yesterday.confirmed);
        change.put(STAT_ENTRIES[3], today.dead - yesterday.confirmed);

        HashMap<String, Map<String, Integer>> ret = new HashMap<>();
        ret.put("cumulative", cumulative);
        ret.put("change", change);
        return ret;
    }

    public Map<String, Map<String, Integer>> getWorldStats() {
        if(!dataReady) {
            return null;
        }
        DataEntry today = worldTimeSeries.get(worldTimeSeries.size() - 1);
        DataEntry yesterday = worldTimeSeries.get(worldTimeSeries.size() - 2);

        HashMap<String, Integer> cumulative = new HashMap<>();
        int curInfected = today.confirmed - today.cured - today.dead;
        cumulative.put(STAT_ENTRIES[0], curInfected);
        cumulative.put(STAT_ENTRIES[1], today.confirmed);
        cumulative.put(STAT_ENTRIES[2], today.cured);
        cumulative.put(STAT_ENTRIES[3], today.dead);

        HashMap<String, Integer> change = new HashMap<>();
        change.put(STAT_ENTRIES[0], curInfected - (yesterday.confirmed - yesterday.cured - yesterday.dead));
        change.put(STAT_ENTRIES[1], today.confirmed - yesterday.confirmed);
        change.put(STAT_ENTRIES[2], today.cured - yesterday.confirmed);
        change.put(STAT_ENTRIES[3], today.dead - yesterday.confirmed);

        HashMap<String, Map<String, Integer>> ret = new HashMap<>();
        ret.put("cumulative", cumulative);
        ret.put("change", change);
        return ret;
    }
}
