package pl.mkcode.springjpaexample.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import java.io.Serializable;

@Entity
@Getter
@Setter
public class Post implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    private String content;
    @Version
    private Long version;

    public Post() {
    }
}
