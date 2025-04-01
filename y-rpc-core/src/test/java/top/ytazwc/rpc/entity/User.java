package top.ytazwc.rpc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 00103943
 * @date 2025-04-01 16:24
 * @package top.ytazwc.rpc.entity
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private int id;
    private String name;

}
