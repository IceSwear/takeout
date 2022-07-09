package com.kk.api;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kk.common.BaseContextOfUser;
import com.kk.common.R;
import com.kk.model.entities.AddressBook;
import com.kk.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** all addressBook APIs are for user -front control
 * @author Spike Wong
 * @since 2022-07-03
 */
@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookAPI {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * add new address
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        //get user-id form thread local
        addressBook.setUserId(BaseContextOfUser.getCurrentId());
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * update address
     *
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<AddressBook> update(@RequestBody AddressBook addressBook) {
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * set new address book
     *
     * @param addressBook
     * @return
     */
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);
        //update condtion,this condtion let all address of user become not default
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContextOfUser.getCurrentId());
        //set default  0=no 1=yes
        wrapper.set(AddressBook::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(wrapper);
        //set this default of this adddressBook =1
        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * search by id
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("Not found the object!");
        }
    }

    /**
     * query deafult address
     */
    @GetMapping("default")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContextOfUser.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (null == addressBook) {
            return R.error("Not found the object");
        } else {
            return R.success(addressBook);
        }
    }

    /**
     * list all address
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        //get current user's id
        addressBook.setUserId(BaseContextOfUser.getCurrentId());
        log.info("addressBook:{}", addressBook);
        //set query condition that userID = user id of addressbook
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        //SQL:select * from address_book where user_id = ? order by update_time desc
        return R.success(addressBookService.list(queryWrapper));
    }


    /**
     * deleted by ids
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids) {
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ids != null, AddressBook::getId, ids);
        addressBookService.remove(updateWrapper);
        return R.success("Delete successfully");
    }
}

