package jp.gr.java_conf.stardiopside.rsnotes.data.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.InsertOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
@ToString(exclude = "password")
@Table("users")
public class User {

    @Id
    private Long id;

    private String username;

    @Nullable
    private String password;

    private boolean enabled;

    @CreatedDate
    @InsertOnlyProperty
    private LocalDateTime createdAt;

    @CreatedBy
    @InsertOnlyProperty
    @Nullable
    private String createdBy;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Nullable
    private String updatedBy;

    @Version
    private Integer version;

}
