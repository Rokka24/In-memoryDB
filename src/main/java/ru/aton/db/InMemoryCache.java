package ru.aton.db;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import ru.aton.exception.RecordNotFoundException;

import java.util.*;

@Slf4j
@Value
public class InMemoryCache {

     Map<Long, List<Record>> accountMap;
     Map<String, List<Record>> nameMap;
     Map<Double, List<Record>> valueMap;

    public InMemoryCache() {
        accountMap = new HashMap<>();
        nameMap = new HashMap<>();
        valueMap = new HashMap<>();
        log.info("In-memory cache has been created");
    }

    public boolean insertRecord(Record record) {
        if (existenceCheck(record)) {
            log.info("Record is already exists in cache");
            return false;
        }

        addToMap(accountMap, record.getAccount(), record);
        addToMap(nameMap, record.getName(), record);
        addToMap(valueMap, record.getValue(), record);
        log.info("Record has been added");
        return true;
    }

    public void deleteRecord(Record record) {
        removeFromMap(accountMap, record.getAccount(), record);
        removeFromMap(nameMap, record.getName(), record);
        removeFromMap(valueMap, record.getValue(), record);
        log.info("Record has been removed");
    }

    public void updateRecord(Record oldRecord, Record updatedRecord) {
        deleteRecord(oldRecord);
        insertRecord(updatedRecord);
        log.info("Record has been updated");
    }

    public List<Record> getRecordByName(String name) {
        List<Record> maybeNullList = nameMap.get(name);
        if (maybeNullList == null)
            throw new RecordNotFoundException("There are no records with such name.");
        return maybeNullList;
    }

    public List<Record> getRecordByValue(double value) {
        List<Record> maybeNullList = valueMap.get(value);
        if (maybeNullList == null)
            throw new RecordNotFoundException("There are no records with such value.");
        return maybeNullList;
    }

    public List<Record> getRecordByAccount(long account) {
        List<Record> maybeNullList = accountMap.get(account);
        if (maybeNullList == null)
            throw new RecordNotFoundException("There are no records with such account.");
        return maybeNullList;
    }

    private boolean existenceCheck(Record record) {
        return accountMap.containsKey(record.getAccount()) && nameMap.containsKey(record.getName()) && valueMap.containsKey(record.getValue());
    }

    private <K> void addToMap(Map<K, List<Record>> indexMap, K key, Record record) {
        indexMap.computeIfPresent(key, (k, v) -> {
            v.add(record);
            return v;
        });
        indexMap.computeIfAbsent(key, k -> new ArrayList<>(List.of(record)));
    }

    private <K> void removeFromMap(Map<K, List<Record>> indexMap, K key, Record recordToRemove) {
        List<Record> records = indexMap.get(key);
        if (records.size() > 1) {
            records.remove(recordToRemove);
        } else
            indexMap.remove(key);
    }
}