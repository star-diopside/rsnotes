package jp.gr.java_conf.stardiopside.rsnotes.data.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.*;
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

    @CreatedDate
    private LocalDateTime createdAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @LastModifiedBy
    private String updatedBy;

}
