import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.aton.db.InMemoryCache;
import ru.aton.db.Record;
import ru.aton.exception.RecordNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InMemoryCacheTest {

    private InMemoryCache cache;
    private final Record record1 = new Record(1L, "name1", 111D);
    private final Record record2 = new Record(12L, "name2", 222D);
    private final Record record3 = new Record(123L, "name3", 33D);
    private final Record recordSameAs3 = new Record(123L, "name3", 33D);
    private final Record record4 = new Record(1L, "name4", 4D);
    private final Record updatedRecord = new Record(0L, "name0", 33D);

    @BeforeEach
    void prepare() {
        cache = new InMemoryCache();
    }


    @Test
    void insertNewRecordAndReturnTrue() {
        assertThat(cache.insertRecord(record1)).isTrue();
    }

    @Test
    void insertExistedRecordAndReturnFalse() {
        cache.insertRecord(record3);
        assertThat(cache.insertRecord(recordSameAs3)).isFalse();
    }

    @Test
    void getListOfRecordsByAccount() {
        cache.insertRecord(record1);
        cache.insertRecord(record2);
        cache.insertRecord(record4);
        assertThat(cache.getRecordByAccount(record1.getAccount())).isEqualTo(List.of(record1, record4));
        assertThat(cache.getRecordByAccount(record2.getAccount())).isEqualTo(List.of(record2));
    }

    @Test
    void getListOfRecordsByValue() {
        cache.insertRecord(record1);
        assertThat(cache.getRecordByValue(record1.getValue())).isEqualTo(List.of(record1));
    }

    @Test
    void getListOfRecordsByName() {
        cache.insertRecord(record1);
        assertThat(cache.getRecordByName(record1.getName())).isEqualTo(List.of(record1));
    }

    @Test
    void updateRecordAndThrowExceptionForOldRecord() {
        cache.insertRecord(record1);
        cache.updateRecord(record1, updatedRecord);
        assertAll(
                () -> assertThrows(RecordNotFoundException.class, () -> cache.getRecordByAccount(record1.getAccount())),
                () -> assertThat(cache.getRecordByAccount(updatedRecord.getAccount())).isEqualTo(List.of(updatedRecord))
        );
    }

    @Test
    void removeRecordsAndThrowException() {
        cache.insertRecord(record2);
        assertThat(cache.getRecordByValue(record2.getValue())).isEqualTo(List.of(record2));
        cache.deleteRecord(record2);
        assertThrows(RecordNotFoundException.class, () -> cache.getRecordByName(record2.getName()));
    }
}
