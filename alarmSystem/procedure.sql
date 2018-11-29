-- ----------------------------
-- Procedure structure for dy_fault_recover
-- ----------------------------
DROP PROCEDURE IF EXISTS `dy_fault_recover`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `dy_fault_recover`(IN `in_ID` int,IN `in_subsystem_id` varchar(10),  IN `in_device_number` varchar(10), OUT `ret_message` varchar(100))
BEGIN
 
	declare source_device_name varchar(100);
	declare source_device_number varchar(100);
	declare target_ID int default 0;

 DECLARE sqlExceptionSignal int default false;
 DECLARE notFoundSignal int default false;
 DECLARE customException int default false;


  
 START TRANSACTION;
 
BEGIN

  DECLARE EXIT HANDLER FOR SQLEXCEPTION SET sqlExceptionSignal = TRUE;
	DECLARE EXIT HANDLER FOR NOT FOUND SET notFoundSignal = TRUE;
	DECLARE EXIT HANDLER FOR SQLSTATE '45000' SET customException = TRUE;


 

		select device_name , device_number into source_device_name , source_device_number    from alarm_record where id = in_ID;

			
			select id into target_ID from alarm_record 
								where subsystem_name = 'DY' 
												and in_ID > id
												and device_number = in_device_number 
												and c_id is null
												and p_id is null
				order by id desc limit 1;
			
			if (target_ID > 0 ) then
				update alarm_record set p_id = target_ID where id = in_ID;
				update alarm_record set c_id = in_ID where id = target_ID;
			end if;

		

END;
	if sqlExceptionSignal then
	   rollback;
       set ret_message = 'SQL失败';
	elseif notFoundSignal then
	   rollback;
	   set ret_message = '未找到';
	elseif customException then
	   rollback;
	   set ret_message = '自定义错误';
	else   
	   commit;
	   set ret_message = '成功';
	end if;


END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for pa_fault_recover
-- ----------------------------
DROP PROCEDURE IF EXISTS `pa_fault_recover`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `pa_fault_recover`(IN `in_ID` int,IN `in_subsystem_id` varchar(10),  IN `in_device_number` varchar(10), OUT `ret_message` varchar(100))
BEGIN
 
	declare source_device_name varchar(100);
	declare source_device_number varchar(100);
	declare target_ID int default 0;

 DECLARE sqlExceptionSignal int default false;
 DECLARE notFoundSignal int default false;
 DECLARE customException int default false;


  
 START TRANSACTION;
 
BEGIN

  DECLARE EXIT HANDLER FOR SQLEXCEPTION SET sqlExceptionSignal = TRUE;
	DECLARE EXIT HANDLER FOR NOT FOUND SET notFoundSignal = TRUE;
	DECLARE EXIT HANDLER FOR SQLSTATE '45000' SET customException = TRUE;


 

		select device_name , device_number into source_device_name , source_device_number    from alarm_record where id = in_ID;

			
			select id into target_ID from alarm_record 
								where subsystem_name = 'PA' 
												and in_ID > id
												and device_number = in_device_number 
												and alarm_description != '正常运行'
												and alarm_description != '在线'
												and alarm_description != '正常'
												and alarm_description != '待机'
												and c_id is null
												and p_id is null
				order by id desc limit 1;
			
			if (target_ID > 0 ) then
				update alarm_record set p_id = target_ID where id = in_ID;
				update alarm_record set c_id = in_ID where id = target_ID;
			end if;

		

END;
	if sqlExceptionSignal then
	   rollback;
       set ret_message = 'SQL失败';
	elseif notFoundSignal then
	   rollback;
	   set ret_message = '未找到';
	elseif customException then
	   rollback;
	   set ret_message = '自定义错误';
	else   
	   commit;
	   set ret_message = '成功';
	end if;


END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for sz_fault_recover
-- ----------------------------
DROP PROCEDURE IF EXISTS `sz_fault_recover`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `sz_fault_recover`(IN `in_ID` int,IN `in_subsystem_id` varchar(10),OUT `ret_message` varchar(100))
BEGIN
 
	declare source_device_name varchar(100);
	declare source_device_number varchar(100);
	declare target_ID int default 0;

 DECLARE sqlExceptionSignal int default false;
 DECLARE notFoundSignal int default false;
 DECLARE customException int default false;


  
 START TRANSACTION;
 
BEGIN

  DECLARE EXIT HANDLER FOR SQLEXCEPTION SET sqlExceptionSignal = TRUE;
	DECLARE EXIT HANDLER FOR NOT FOUND SET notFoundSignal = TRUE;
	DECLARE EXIT HANDLER FOR SQLSTATE '45000' SET customException = TRUE;


 

		select device_name , device_number into source_device_name , source_device_number    from alarm_record where id = in_ID;

		
		
		select id into target_ID from alarm_record
			where subsystem_name = 'SZ'
						and in_ID > id 
						and device_name = source_device_name 
						and device_number = source_device_number
						and alarm_description_code != 'NORMAL'
						and alarm_description_code != 'FAULTREP'
						and c_id is null
						and p_id is null
		order by id desc limit 1;
	 
		if (target_ID >0 ) then
			update alarm_record set p_id = target_ID where id = in_ID;
			update alarm_record set c_id = in_ID where id = target_ID;
		end if;
		

END;
	if sqlExceptionSignal then
	   rollback;
       set ret_message = 'SQL失败';
	elseif notFoundSignal then
	   rollback;
	   set ret_message = '未找到';
	elseif customException then
	   rollback;
	   set ret_message = '自定义错误';
	else   
	   commit;
	   set ret_message = '成功';
	end if;


END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for zydh_fault_recover
-- ----------------------------
DROP PROCEDURE IF EXISTS `zydh_fault_recover`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `zydh_fault_recover`(IN `in_ID` int,IN `in_subsystem_id` varchar(10),  IN `in_zydh_error_code` varchar(10), OUT `ret_message` varchar(100))
BEGIN
 
	declare source_device_name varchar(100);
	declare source_device_number varchar(100);
	declare target_ID int default 0;

 DECLARE sqlExceptionSignal int default false;
 DECLARE notFoundSignal int default false;
 DECLARE customException int default false;

-- declare ret_message varchar(100);
  
 START TRANSACTION;
 
BEGIN

  DECLARE EXIT HANDLER FOR SQLEXCEPTION SET sqlExceptionSignal = TRUE;
	DECLARE EXIT HANDLER FOR NOT FOUND SET notFoundSignal = TRUE;
	DECLARE EXIT HANDLER FOR SQLSTATE '45000' SET customException = TRUE;


		-- 2.找到0x1XXXH对应的ID
		select id into target_ID from alarm_record 
					where subsystem_name = 'ZYDH' 
							and in_ID > id 
							and alarm_description_code = in_zydh_error_code 
							and c_id is null 
							and p_id is null
					order by id desc limit 1;
		-- 3.更新
		if (target_ID > 0 ) then
			update alarm_record set p_id = target_ID where id = in_ID;
			update alarm_record set c_id = in_ID where id = target_ID;
		end if;

		

END;
	if sqlExceptionSignal then
	   rollback;
       set ret_message = 'SQL失败';
	elseif notFoundSignal then
	   rollback;
	   set ret_message = '未找到';
	elseif customException then
	   rollback;
	   set ret_message = '自定义错误';
	else   
	   commit;
	   set ret_message = '成功';
	end if;


END
;;
DELIMITER ;
