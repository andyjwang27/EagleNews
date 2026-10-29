import SwiftUI
import FirebaseFirestore
import PhotosUI
import FirebaseStorage

struct AddArticleView: View {
    @StateObject var viewModel = AddArticleViewViewModel()
    @State private var selectedPhotos: [PhotosPickerItem] = []
    @State private var selectedImages: [UIImage] = []
    @FocusState private var isTyping: Bool
    @State private var selectedRoles: Set<RoleItem> = []
    
    public var body: some View {
        ZStack {
            VStack(spacing: 0) {
                Color.clear
                    .contentShape(Rectangle())
                    .frame(height: 150)
                    .padding(.leading, 60)
                    .onTapGesture {
                        isTyping = false
                    }
                Spacer()
            }
            Form {
                Section(header:
                    VStack(spacing: 0) {
                        Spacer().frame(height: 50)
                    }
                ){
                    TextField("Title", text: $viewModel.title, axis: .vertical)
                        .textFieldStyle(OutlinedTextFieldStyle())
                        .autocorrectionDisabled()
                        .textInputAutocapitalization(.never)
                        .focused($isTyping)
                        .listRowInsets(EdgeInsets(top: 4, leading: 16, bottom: 0, trailing: 16))
                        .padding(.vertical, 10)
                    TextField("Summary", text: $viewModel.summary, axis: .vertical)
                        .textFieldStyle(OutlinedTextFieldStyle())
                        .autocorrectionDisabled()
                        .textInputAutocapitalization(.never)
                        .focused($isTyping)
                        .listRowInsets(EdgeInsets(top: 0, leading: 16, bottom: 4, trailing: 16))
                        .padding(.bottom, 10);
                    DatePicker("Published Date:", selection: $viewModel.publishedAt, displayedComponents: [.date, .hourAndMinute])
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Select Roles:")
                        Button {
                            viewModel.selectRolesView = true
                        } label: {
                            buttonLabel(title: "Select Roles")
                                .contentShape(Rectangle())
                        }
                        .listRowInsets(EdgeInsets(top: 0, leading: 16, bottom: 0, trailing: 16))
                        .frame(width: 150)
                        .buttonStyle(.borderless)
                        selectedRolesView
                    }
                    PhotosPicker(
                        selection: $selectedPhotos,
                        maxSelectionCount: 10,
                        matching: .images
                    ) {
                        Label("Attach Photos", systemImage: "photo")
                            .foregroundColor(.navy)
                    }
                    if !selectedImages.isEmpty {
                        ScrollView(.horizontal) {
                            HStack(spacing: 10) {
                                ForEach(Array(selectedImages.enumerated()), id: \.offset) { index, image in
                                    ZStack(alignment: .topTrailing) {
                                        Image(uiImage: image)
                                            .resizable()
                                            .scaledToFill()
                                            .frame(width: 150, height: 250)
                                            .clipped()
                                            .cornerRadius(12)
                                        Button {
                                            selectedImages.remove(at: index)
                                        } label: {
                                            Image(systemName: "xmark.circle.fill")
                                                .font(.title3)
                                                .foregroundStyle(.white, .red)
                                                .background(Circle().fill(.black.opacity(0.3)))
                                        }
                                        .padding(5)
                                    }
                                }
                            }
                        }
                        .frame(maxHeight: 250)
                    }
                    TLButton(title: "Publish", background: Color.navy) {
                        isTyping = false
                        let roleNames = selectedRoles.map{ $0.name }
                        viewModel.tags = roleNames
                        
                        if !selectedImages.isEmpty {
                            viewModel.uploadImages(images: selectedImages) { urls in
                                viewModel.addArticle(imageURLs: urls) { success in
                                    if success {
                                        viewModel.activeAlert = .success
                                        selectedImages = []
                                        selectedRoles = []
                                    } else {
                                        viewModel.activeAlert = .error
                                    }
                                }
                            }
                        } else {
                            viewModel.addArticle(imageURLs: []) { success in
                                if success {
                                    viewModel.activeAlert = .success
                                    selectedRoles = []
                                } else {
                                    viewModel.activeAlert = .error
                                }
                            }
                        }
                    }
                    .fixedSize(horizontal: false, vertical: true)
                }
                .listRowSeparator(.hidden)
            }
            .sheet(isPresented: $viewModel.selectRolesView) {
                SelectRolesView(selected: $selectedRoles)
            }
            .alert(item: $viewModel.activeAlert) { alert in
                switch alert {
                case .error:
                    return Alert(
                        title: Text("Error"),
                        message: Text(viewModel.errorMessage)
                    )
                case .success:
                    return Alert(
                        title: Text("Success"),
                        message: Text("Article saved successfully."),
                        dismissButton: .default(Text("OK")) {
                            isTyping = false
                        }
                    )
                }
            }
            .onChange(of: selectedPhotos) { _, newPhotos in
                Task {
                    var images: [UIImage] = []

                    for photo in newPhotos {
                        if let data = try? await photo.loadTransferable(type: Data.self),
                           let image = UIImage(data: data) {
                            images.append(image)
                        }
                    }

                    selectedImages = images
                }
            }
            .onAppear {
                viewModel.fetchUser()
            }
            .padding(.top, 150)
            .padding(.bottom, 150)
            .ignoresSafeArea(.container, edges: [.top, .bottom])
            .ignoresSafeArea(.keyboard)
        }
    }
    
    @ViewBuilder
    private var selectedRolesView: some View {
        if !selectedRoles.isEmpty {
            VStack(alignment: .leading, spacing: 10) {
                Text("Selected Roles:")

                WrappingHStack(horizontalSpacing: 8, verticalSpacing: 10) {
                    ForEach(Array(selectedRoles)) { role in
                        roleChip(role)
                    }
                }
                .fixedSize(horizontal: false, vertical: true)
            }
            .padding(.top, 10)
        }
    }

    private func roleChip(_ role: RoleItem) -> some View {
        HStack(spacing: 6) {
            Text(role.name.formattedString())

            Button {
                selectedRoles.remove(role)
            } label: {
                Image(systemName: "xmark")
                    .tint(.navy)
            }
            .buttonStyle(.borderless)
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 8)
        .background(Color(.systemGray5))
        .clipShape(RoundedRectangle(cornerRadius: 20))
    }
}

private func buttonLabel(title: String) -> some View {
    ZStack {
        RoundedRectangle(cornerRadius: 10)
            .foregroundColor(.navy)

        HStack(spacing: 10) {
            Text(title)
                .foregroundColor(.white)
                .font(.system(size: 15))
                .bold()

            Image(systemName: "plus")
                .foregroundColor(.white)
        }
    }
    .frame(height: 45)
}
