package com.ra.util;

import com.ra.config.Validator;
import com.ra.model.OrderStatus;
import com.ra.model.Orders;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static com.ra.config.Config.*;
import static com.ra.config.Validator.*;

public class Test {
    public static void main(String[] args) {

        String path = "C:\\Users\\aquar\\Desktop\\Excel\\INB_ImportData.xlsx";
        File file = new File(path);
//        try {
//            FileInputStream fileInputStream = new FileInputStream(file);
//            Workbook workbook = new XSSFWorkbook(fileInputStream);
//            Sheet sheet = workbook.getSheetAt(0);
//
//            Map<String, String> errors = new HashMap<>();
//            List<Orders> ordersList = new ArrayList<>();
//
//            Iterator<Row> rowIterator = sheet.rowIterator();
//            while (rowIterator.hasNext()) {
//                Row row = rowIterator.next();
//                if (row.getRowNum() < 2 || row.getCell(row.getFirstCellNum()).getCellType().equals(CellType.BLANK)) {
//                    continue;
//                }
//                Iterator<Cell> cellIterator = row.cellIterator();
//                Orders orders = new Orders();
//                while (cellIterator.hasNext()) {
//                    //Read cell
//                    Cell cell = cellIterator.next();
//                    Object cellValueObject = getCellValue(cell);
//                    if (cellValueObject == null || cellValueObject.toString().isEmpty()) {
//                        break;
//                    }
//                    String cellValue = cellValueObject.toString();
//                    // Set value for  object
//                    int columnIndex = cell.getColumnIndex();
//                    switch (columnIndex) {
//                        case COLUMN_PROVIDER_NAME:
//                            if (!Validator.isNotEmpty(cellValue)) {
//                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "Ten khong duoc de trong");
//                                break;
//                            }
//                            orders.setProviderName(cellValue);
//                            break;
//                        case COLUMN_PROVIDER_PHONE:
//                            if (!Validator.isValidPhone(cellValue)) {
//                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "So dien thoai Khong hop le");
//                                break;
//                            }
//                            orders.setProviderPhone(cellValue);
//                            break;
//                        case COLUMN_PROVIDER_ADDRESS:
//                            if (!Validator.isNotEmpty(cellValue)) {
//                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "dia chi khong duoc de trong");
//                                break;
//                            }
//                            orders.setProviderAddress(cellValue);
//                            break;
//                        case COLUMN_PROVIDER_EMAIL:
//                            if (!Validator.isValidEmail(cellValue)) {
//                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "email khong hop le");
//                                break;
//                            }
//                            orders.setProviderEmail(cellValue);
//                            break;
//                        case COLUMN_PROVIDER_LONGITUDE:
//                            orders.setProviderLongitude(Double.valueOf(cellValue));
//                            break;
//                        case COLUMN_PROVIDER_LATITUDE:
//                            orders.setProviderLatitude(Double.valueOf(cellValue));
//                            break;
//                        case COLUMN_RECEIVER_NAME:
//                            if (!Validator.isNotEmpty(cellValue)) {
//                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "Ten khong duoc de trong");
//                                break;
//                            }
//                            orders.setReceiverName(cellValue);
//                            break;
//                        case COLUMN_RECEIVER_PHONE:
//                            if (!Validator.isValidPhone(cellValue)) {
//                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "So dien thoai Khong hop le");
//                                break;
//                            }
//                            orders.setReceiverPhone(cellValue);
//                            break;
//                        case COLUMN_RECEIVER_ADDRESS:
//                            if (!Validator.isNotEmpty(cellValue)) {
//                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "dia chi khong duoc de trong");
//                                break;
//                            }
//                            orders.setReceiverAddress(cellValue);
//                            break;
//                        case COLUMN_RECEIVER_EMAIL:
//                            if (!Validator.isValidEmail(cellValue)) {
//                                errors.put((row.getRowNum() + 1) + ALPHABET[cell.getColumnIndex()], "email Khong hop le");
//                                break;
//                            }
//                            orders.setReceiverEmail(cellValue);
//                            break;
//                        case COLUMN_RECEIVER_LONGITUDE:
//                            orders.setReceiverLongitude(Double.valueOf(cellValue));
//                            break;
//                        case COLUMN_RECEIVER_LATITUDE:
//                            orders.setReceiverLatitude(Double.valueOf(cellValue));
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            ordersList.add(orders);
//            }
//            System.out.println(ordersList);
//            if (!errors.isEmpty()) {
//                for (String e: errors.keySet()
//                     ) {
//                    System.out.printf("%s, %s",e,errors.get(e));
//                    System.out.println();
//                }
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}

