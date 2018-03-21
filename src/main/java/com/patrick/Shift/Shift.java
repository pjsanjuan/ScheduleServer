package com.patrick.Shift;

import com.patrick.Task.Task;
import com.patrick.User.User;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SHIFT_ID")
    private Long id;

    @Column(name = "START", nullable = false)
    private OffsetDateTime start;

    @Column(name = "END", nullable = false)
    private OffsetDateTime end;

    @ManyToOne(optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "TASK_ID", nullable = false)
    private Task task;

    public Shift() {
    }

    public Shift(OffsetDateTime start, OffsetDateTime end, User user, Task task) {
        this.start = start;
        this.end = end;
        this.user = user;
        this.task = task;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OffsetDateTime getStart() {
        return start;
    }

    public void setStart(OffsetDateTime start) {
        this.start = start;
    }

    public OffsetDateTime getEnd() {
        return end;
    }

    public void setEnd(OffsetDateTime end) {
        this.end = end;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }


    @Override
    public String toString() {
        return "Shift{" +
                "id=" + id +
                ", start=" + start +
                ", end=" + end +
                ", user=" + user +
                ", task=" + task +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shift shift = (Shift) o;
        return Objects.equals(id, shift.id) &&
                Objects.equals(start, shift.start) &&
                Objects.equals(end, shift.end) &&
                Objects.equals(user, shift.user) &&
                Objects.equals(task, shift.task);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, start, end, user, task);
    }
}
