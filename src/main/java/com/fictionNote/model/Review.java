package com.fictionNote.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@ToString
public class Review {
    public Review(){}
    String type;
    String content;
    @Getter
    boolean checked;
    String info;
    String user;
    @Id
    String id;
}
