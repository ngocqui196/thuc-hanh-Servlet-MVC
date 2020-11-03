package controller;

import model.Customer;
import service.CustomerService;
import service.CustomerServiceImpl;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "CustomerServlet", urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {

//    đối tượng CustomerServiceImpl dùng để truy xuất đến dữ liệu.
    private CustomerService customerService = new CustomerServiceImpl();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if(action == null){
            action = "";
        }
        switch (action){
            case "create":
//                Điều hướng việc xử lý lưu trữ khách hàng mới:
                createCustomer(request, response);
                break;
            case "edit":
//                Điều hướng việc xử lý thao tác cập nhật thông tin khách hàng:
                updateCustomer(request, response);
                break;
            case "delete":
                deleteCustomer(request,response);
                break;
            default:
                break;
        }
    }

//    Phương thức updateCustomer(request, response)
//    sẽ thực hiện thao tác cập nhật thông tin khách hàng vào trong danh sách khách hàng giả lập:
    private void updateCustomer(HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        Customer customer = this.customerService.findById(id);
        RequestDispatcher dispatcher;
        if(customer == null){
            dispatcher = request.getRequestDispatcher("error-404.jsp");
        } else {
            customer.setName(name);
            customer.setEmail(email);
            customer.setAddress(address);
            this.customerService.update(id, customer);
            request.setAttribute("customer", customer);
            request.setAttribute("message", "Customer information was updated");
            dispatcher = request.getRequestDispatcher("customer/edit.jsp");
        }
        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    Phương thức createCustomer(request, response) sẽ nhận về các tham số được gửi lên từ form;
//    khởi tạo đối tượng Customer và lưu trữ vào trong một danh sách khách hàng giả lập.
    private void createCustomer(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        int id = (int)(Math.random() * 10000);

        Customer customer = new Customer(id, name, email, address);
        this.customerService.save(customer);

        RequestDispatcher dispatcher = request.getRequestDispatcher("customer/create.jsp");
        request.setAttribute("message", "New customer was created");
        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    Phương thức deleteCustomer(request,response);
    private void deleteCustomer(HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(request.getParameter("id"));
        Customer customer = this.customerService.findById(id);
        RequestDispatcher dispatcher;
        if(customer == null){
            dispatcher = request.getRequestDispatcher("error-404.jsp");
        } else {
            this.customerService.remove(id);
            try {
                response.sendRedirect("/customer");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if(action == null){
            action = "";
        }
        switch (action){
            case "create":
//                Điều hướng việc hiển thị form tạo khách hàng mới
                showCreateForm(request, response);
                break;
            case "edit":
//                Điều hướng hiển thị form cập nhật thông tin khách hàng:
                showEditForm(request, response);
                break;
            case "delete":
                showDeleteForm(request,response);
                break;
            case "view":
                viewCustomer(request,response);
                break;
            default:

//                 Điều hướng "action" danh sách khách hàng về phương thức listCustomers(request, response);
                listCustomers(request, response);
                break;
        }
    }

//    Phương thức showEditForm(request, response) sẽ tìm một khách hàng theo id được truyền vào.
//    Nếu khách hàng không tồn tại thì sẽ hiển thị thông báo lỗi.
//    Nếu khách hàng có tồn tại thì hiển thị thông tin khách hàng lên một view là customer/edit.jsp:
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(request.getParameter("id"));
        Customer customer = this.customerService.findById(id);
        RequestDispatcher dispatcher;
        if(customer == null){
            dispatcher = request.getRequestDispatcher("error-404.jsp");
        } else {
            request.setAttribute("customer", customer);
            dispatcher = request.getRequestDispatcher("customer/edit.jsp");
        }
        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //  Phương thức showCreateForm(request, response);
//  sẽ trả về một form được định nghĩa trong view customer/create.jsp:
    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) {
        RequestDispatcher dispatcher = request.getRequestDispatcher("customer/create.jsp");
        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//  Phương thức listCustomers(request, response)
//  nhận về danh sách khách hàng và chuyển sang view customer/list.jsp để hiển thị.
    private void listCustomers(HttpServletRequest request, HttpServletResponse response) {
        List<Customer> customers = this.customerService.findAll();
        request.setAttribute("customers", customers);

        RequestDispatcher dispatcher = request.getRequestDispatcher("customer/list.jsp");
        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    Tương tự với các thao tác trước, chúng ta lần lượt triển khai tiếp tính năng xoá khách hàng:
//    Phương thức showDeleteForm(request, response):
    private void showDeleteForm(HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(request.getParameter("id"));
        Customer customer = this.customerService.findById(id);
        RequestDispatcher dispatcher;
        if(customer == null){
            dispatcher = request.getRequestDispatcher("error-404.jsp");
        } else {
            request.setAttribute("customer", customer);
            dispatcher = request.getRequestDispatcher("customer/delete.jsp");
        }
        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    Phương thức viewCustomer(request, response):

    private void viewCustomer(HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(request.getParameter("id"));
        Customer customer = this.customerService.findById(id);
        RequestDispatcher dispatcher;
        if(customer == null){
            dispatcher = request.getRequestDispatcher("error-404.jsp");
        } else {
            request.setAttribute("customer", customer);
            dispatcher = request.getRequestDispatcher("customer/view.jsp");
        }
        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
