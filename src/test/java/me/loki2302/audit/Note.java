package me.loki2302.audit;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class) // required
public class Note {
    @Id
    @GeneratedValue
    public Long id;

    @CreatedBy
    public String createdBy;

    @CreatedDate
    public Date createdAt;

    @LastModifiedBy
    public String modifiedBy;

    @LastModifiedDate
    public Date modifiedAt;

    public String content;
}
