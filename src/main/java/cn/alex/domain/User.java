package cn.alex.domain;

import lombok.*;

/**
 * Created by WCY on 2021/4/5
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    private String name;

    private Integer age;

    private String email;

}
