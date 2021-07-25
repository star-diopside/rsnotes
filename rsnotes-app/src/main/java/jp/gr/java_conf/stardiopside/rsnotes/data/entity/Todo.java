package jp.gr.java_conf.stardiopside.rsnotes.data.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
@Table("todos")
public class Todo {

    @Id
    private Integer id;

    @Length(max = 2048)
    private String text;

    @Column("created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column("created_by")
    @CreatedBy
    private String createdBy;

    @Column("updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column("updated_by")
    @LastModifiedBy
    private String updatedBy;

}
