package com.shell.core.convert;

import java.util.List;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/16 22:51
 * @Description
 */
public interface IConvert<D, P> {

    D po2Dto(P p);

    P dto2Po(D d);

    List<D> po2DtoList(List<P> pList);

    List<P> dto2PoList(List<D> dList);

}
