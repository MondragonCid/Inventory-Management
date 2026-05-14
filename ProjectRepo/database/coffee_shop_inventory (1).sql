-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 14, 2026 at 11:49 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `coffee_shop_inventory`
--

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `name`, `description`) VALUES
(1, 'Hot Drinks', 'Espresso, brewed, and tea-based hot beverages'),
(2, 'Cold Drinks', 'Iced, blended, and chilled beverages'),
(3, 'Pastries', 'Breads, cakes, and baked goods'),
(4, 'Syrups', 'Flavoring syrups and sauces'),
(5, 'Supplies', 'Cups, lids, straws, and packaging'),
(19, 'Baked Cakes', 'Chocolate Cake');

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `category_id` int(11) DEFAULT NULL,
  `supplier_id` int(11) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL DEFAULT 0.00,
  `stock` int(11) NOT NULL DEFAULT 0,
  `unit` varchar(30) NOT NULL DEFAULT 'pcs',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`id`, `name`, `category_id`, `supplier_id`, `price`, `stock`, `unit`, `created_at`) VALUES
(1, 'Caramel Macchiato', 1, 1, 185.00, 50, 'cup', '2026-05-13 21:09:04'),
(2, 'Brewed Coffee', 1, 1, 95.00, 100, 'cup', '2026-05-13 21:09:04'),
(3, 'Iced Americano', 2, 1, 130.00, 75, 'cup', '2026-05-13 21:09:04'),
(4, 'Matcha Latte', 2, 1, 150.00, 60, 'cup', '2026-05-13 21:09:04'),
(5, 'Chocolate Croissant', 3, 2, 85.00, 7, 'pcs', '2026-05-13 21:09:04'),
(6, 'Blueberry Muffin', 3, 2, 75.00, 239, 'pcs', '2026-05-13 21:09:04'),
(8, 'Paper Cups 12oz', 5, 3, 350.00, 5, 'pack', '2026-05-13 21:09:04'),
(9, 'Test Drink', 1, 1, 99.00, 25, 'cup', '2026-05-13 21:09:10'),
(19, 'asdsadas', 1, 1, 13213.00, 118213218, 'pcs', '2026-05-14 18:44:06'),
(20, 'Test Drink', 1, 1, 99.00, 25, 'cup', '2026-05-14 18:46:20'),
(25, 'Testing Stock', 1, 2, 200.00, 22, 'pcs', '2026-05-14 21:07:57');

-- --------------------------------------------------------

--
-- Table structure for table `stock_in`
--

CREATE TABLE `stock_in` (
  `id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `stock_in`
--

INSERT INTO `stock_in` (`id`, `product_id`, `quantity`, `remarks`, `user_id`, `created_at`) VALUES
(1, 1, 50, 'Initial stock', 1, '2026-05-13 21:09:04'),
(2, 2, 100, 'Initial stock', 1, '2026-05-13 21:09:04'),
(3, 3, 75, 'Initial stock', 1, '2026-05-13 21:09:04'),
(4, 4, 60, 'Initial stock', 1, '2026-05-13 21:09:04'),
(5, 5, 30, 'Initial stock', 1, '2026-05-13 21:09:04'),
(6, 6, 8, 'Initial stock', 1, '2026-05-13 21:09:04'),
(8, 8, 5, 'Initial stock', 1, '2026-05-13 21:09:04'),
(11, 6, 20, 'Debugger test delivery', 1, '2026-05-13 21:37:20'),
(12, 6, 20, 'Debugger test delivery', 1, '2026-05-13 21:38:16'),
(13, 6, 20, 'Debugger test delivery', 1, '2026-05-14 07:30:52'),
(14, 6, 20, 'Debugger test delivery', 1, '2026-05-14 07:31:19'),
(15, 6, 20, 'Debugger test delivery', 1, '2026-05-14 07:31:26'),
(16, 6, 20, 'Debugger test delivery', 1, '2026-05-14 09:03:26'),
(17, 19, 20, 'Debugger test delivery', 1, '2026-05-14 18:46:20'),
(18, 19, 20, 'Debugger test delivery', 1, '2026-05-14 19:02:02'),
(19, 19, 20, 'Debugger test delivery', 1, '2026-05-14 19:02:04'),
(20, 19, 20, 'Debugger test delivery', 1, '2026-05-14 19:02:12'),
(21, 19, 20, 'Debugger test delivery', 1, '2026-05-14 20:36:17'),
(22, 6, 123, 'good', 1, '2026-05-14 21:06:26'),
(23, 25, 2, 'Very Good', 1, '2026-05-14 21:09:35');

-- --------------------------------------------------------

--
-- Table structure for table `stock_out`
--

CREATE TABLE `stock_out` (
  `id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `stock_out`
--

INSERT INTO `stock_out` (`id`, `product_id`, `quantity`, `remarks`, `user_id`, `created_at`) VALUES
(1, 1, 5, 'Morning sales', 2, '2026-05-13 21:09:04'),
(2, 2, 12, 'Morning sales', 2, '2026-05-13 21:09:04'),
(3, 3, 8, 'Afternoon sales', 2, '2026-05-13 21:09:04'),
(6, 6, 2, 'Debugger test sale', 1, '2026-05-13 21:37:20'),
(7, 6, 2, 'Debugger test sale', 1, '2026-05-13 21:38:16'),
(8, 6, 2, 'Debugger test sale', 1, '2026-05-14 07:30:52'),
(9, 6, 2, 'Debugger test sale', 1, '2026-05-14 07:31:19'),
(10, 6, 2, 'Debugger test sale', 1, '2026-05-14 07:31:26'),
(11, 6, 2, 'Debugger test sale', 1, '2026-05-14 09:03:26'),
(12, 19, 2, 'Debugger test sale', 1, '2026-05-14 18:46:20'),
(13, 19, 999999, 'Should fail', 1, '2026-05-14 18:46:20'),
(14, 19, 2, 'Debugger test sale', 1, '2026-05-14 19:02:02'),
(15, 19, 999999, 'Should fail', 1, '2026-05-14 19:02:02'),
(16, 19, 2, 'Debugger test sale', 1, '2026-05-14 19:02:04'),
(17, 19, 999999, 'Should fail', 1, '2026-05-14 19:02:04'),
(18, 19, 2, 'Debugger test sale', 1, '2026-05-14 19:02:12'),
(19, 19, 999999, 'Should fail', 1, '2026-05-14 19:02:12'),
(20, 19, 2, 'Debugger test sale', 1, '2026-05-14 20:36:17'),
(21, 19, 999999, 'Should fail', 1, '2026-05-14 20:36:17'),
(22, 5, 23, 'Wasted / Spoiled', 1, '2026-05-14 21:09:48');

-- --------------------------------------------------------

--
-- Table structure for table `suppliers`
--

CREATE TABLE `suppliers` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `contact_name` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `suppliers`
--

INSERT INTO `suppliers` (`id`, `name`, `contact_name`, `phone`, `email`, `address`) VALUES
(1, 'Bean Brothers PH', 'Carlos Reyes', '09171234567', 'carlos@beanbrothers.ph', 'Cebu City'),
(2, 'Sweet Supply Co.', 'Ana Lim', '09281234567', 'ana@sweetsupply.ph', 'Mandaue City'),
(3, 'Pack and Go', 'Ben Torres', '09391234567', 'ben@packandgo.ph', 'Lapu-Lapu City');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMIN','STAFF') NOT NULL DEFAULT 'STAFF',
  `full_name` varchar(100) NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `role`, `full_name`, `is_active`, `created_at`) VALUES
(1, 'admin', 'admin123', 'ADMIN', 'System Administrator', 1, '2026-05-13 21:09:04'),
(2, 'staff01', 'staff123', 'STAFF', 'Juan Dela Cruz', 1, '2026-05-13 21:09:04'),
(3, 'staff02', 'staff123', 'STAFF', 'Maria Santos', 1, '2026-05-13 21:09:04');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_product_category` (`category_id`),
  ADD KEY `fk_product_supplier` (`supplier_id`);

--
-- Indexes for table `stock_in`
--
ALTER TABLE `stock_in`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_stockin_product` (`product_id`),
  ADD KEY `fk_stockin_user` (`user_id`);

--
-- Indexes for table `stock_out`
--
ALTER TABLE `stock_out`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_stockout_product` (`product_id`),
  ADD KEY `fk_stockout_user` (`user_id`);

--
-- Indexes for table `suppliers`
--
ALTER TABLE `suppliers`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT for table `stock_in`
--
ALTER TABLE `stock_in`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT for table `stock_out`
--
ALTER TABLE `stock_out`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `suppliers`
--
ALTER TABLE `suppliers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_product_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `stock_in`
--
ALTER TABLE `stock_in`
  ADD CONSTRAINT `fk_stockin_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_stockin_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `stock_out`
--
ALTER TABLE `stock_out`
  ADD CONSTRAINT `fk_stockout_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_stockout_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
