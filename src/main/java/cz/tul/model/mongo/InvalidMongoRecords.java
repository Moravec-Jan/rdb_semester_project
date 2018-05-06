package cz.tul.model.mongo;

import cz.tul.model.generic.InvalidRecords;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Document(collection = InvalidRecords.TABLE_NAME)
public class InvalidMongoRecords implements InvalidRecords {

    @Id
    private Integer id;
    private int pocet;

    public InvalidMongoRecords() {
    }

    public InvalidMongoRecords(int id, int pocet) {
        this.id = id;
        this.pocet = pocet;
    }

    @Override
    public int getId() {
        return id;
    }

    public int getPocet() {
        return pocet;
    }

    public void setPocet(int pocet) {
        this.pocet = pocet;
    }
}
