package de.objectcode.time4u.migrator.server04.old.entities;
// Generated 06.10.2008 17:02:23 by Hibernate Tools 3.2.0.CR1


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * OldTaskIdmap generated by hbm2java
 */
@Entity
@Table(name="TASK_IDMAP"
)
public class OldTaskIdmap  implements java.io.Serializable {

  private static final long serialVersionUID = 1L;
  
     private Long id;
     private long otherId;
     private long taskId;
     private long foreignserverId;

    public OldTaskIdmap() {
    }

    public OldTaskIdmap(long otherId, long taskId, long foreignserverId) {
       this.otherId = otherId;
       this.taskId = taskId;
       this.foreignserverId = foreignserverId;
    }
   
     @Id @GeneratedValue(strategy=IDENTITY)
    
    @Column(name="id", unique=true, nullable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @Column(name="otherId", nullable=false)
    public long getOtherId() {
        return this.otherId;
    }
    
    public void setOtherId(long otherId) {
        this.otherId = otherId;
    }
    
    @Column(name="task_id", nullable=false)
    public long getTaskId() {
        return this.taskId;
    }
    
    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }
    
    @Column(name="foreignserver_id", nullable=false)
    public long getForeignserverId() {
        return this.foreignserverId;
    }
    
    public void setForeignserverId(long foreignserverId) {
        this.foreignserverId = foreignserverId;
    }




}

