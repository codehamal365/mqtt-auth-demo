package com.example.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xie.wei
 * @date created at 2021-12-04 20:14
 */
@AllArgsConstructor
@Getter
public class Tuple<F, S> {
    private final F first;
    private final S second;
}
