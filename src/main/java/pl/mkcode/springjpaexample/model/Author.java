package pl.mkcode.springjpaexample.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
public class Author {
    @Id
    private Long id;
    private String firstName;
    private String lastName;

    @Version
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public Author() {
    }
}
