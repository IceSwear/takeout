package com.kk.service.impl;

import com.kk.model.entities.AddressBook;
import com.kk.model.persistence.AddressBookMapper;
import com.kk.service.AddressBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author Spike Wong
 * @since 2022-07-03
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

}
